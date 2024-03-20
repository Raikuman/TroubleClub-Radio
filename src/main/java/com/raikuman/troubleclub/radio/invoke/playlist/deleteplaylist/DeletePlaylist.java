package com.raikuman.troubleclub.radio.invoke.playlist.deleteplaylist;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.pagination.Pagination;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeletePlaylist extends Command {

    @Override
    public void handle(CommandContext ctx) {
        List<com.raikuman.troubleclub.radio.music.playlist.Playlist> playlists = new ArrayList<>();
        if (ctx.args().isEmpty()) {
            // Get all playlists
            playlists = PlaylistDatabaseHandler.getPlaylists(ctx.event().getAuthor());
        } else {
            // Build query from args
            StringBuilder argBuilder = new StringBuilder();
            for (int i = 0; i < ctx.args().size(); i++) {
                if (i != 0) {
                    argBuilder.append("* + ");
                }

                argBuilder.append(ctx.args().get(i));
            }

            // Get playlists from args
            try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                    "SELECT playlist_id, name, songs FROM playlist_fts WHERE playlist_fts MATCH ? LIMIT 5"
                )) {
                statement.setString(1, argBuilder + "*");
                statement.setInt(1, DefaultDatabaseHandler.getUserId(ctx.event().getAuthor()));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        playlists.add(new com.raikuman.troubleclub.radio.music.playlist.Playlist(
                            resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getInt(3),
                            ctx.event().getAuthor()
                        ));
                    }
                }
            } catch (SQLException e) {
                MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                    EmbedResources.error("An error occurred retrieving cassettes!", "Could not get cassette from database.",
                        ctx.event().getChannel(), ctx.event().getAuthor()));
                return;
            }

            if (playlists.isEmpty()) {
                MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                    EmbedResources.error("Could not retrieve any cassettes!", "`" + argBuilder + "` did not find any " +
                        "results.", ctx.event().getChannel(), ctx.event().getAuthor()));
                return;
            }
        }

        if (playlists == null) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error(
                    "There was an error getting your cassettes!",
                    "Could not retrieve cassettes from the database.",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));
            return;
        }

        if (playlists.isEmpty()) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                    MusicManager.CASSETTE_COLOR,
                    "\uD83D\uDCFC You do not have any cassettes!",
                    "",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));
            ctx.event().getMessage().delete().queue();
            return;
        }


        List<EmbedBuilder> pages = PlaylistUtils.getPlaylistPages(
            playlists, ctx.event().getChannel(), ctx.event().getAuthor());

        int iterator = 0;
        for (EmbedBuilder page : pages) {
            page.addField("Id", String.valueOf(playlists.get(iterator).getId()), true);
            iterator++;
        }

        // Build pages
        new Pagination(
            ctx.event().getAuthor(),
            "\uD83D\uDCFC Found Cassettes to Delete",
            ((messageChannelUnion, user) -> pages),
            componentHandler)
            .setLooping(true)
            .setHasFirstPage(true)
            .setExtraButtons(new DeletePlaylistButton())
            .sendPagination(ctx);
    }

    @Override
    public String getInvoke() {
        return "deletecassette";
    }

    @Override
    public List<String> getAliases() {
        return List.of("dc", "deletec");
    }

    @Override
    public String getUsage() {
        return "<cassette name>";
    }

    @Override
    public String getDescription() {
        return "Delete a cassette from your library.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Playlist());
    }
}
