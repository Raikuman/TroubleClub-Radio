package com.raikuman.troubleclub.radio.invoke.playlist.deleteplaylist;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.pagination.Pagination;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
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
        if (ctx.args().isEmpty()) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            return;
        }

        // Check if arg is id
        if (ctx.args().size() == 1) {
            int playlistId;
            try {
                playlistId = Integer.parseInt(ctx.args().get(0));
            } catch (NumberFormatException e) {
                playlistId = -1;
            }

            if (playlistId > 0) {

            } else {
                deleteByName(ctx);
            }
        } else {
            deleteByName(ctx);
        }
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
        return "<cassette #/name>";
    }

    @Override
    public String getDescription() {
        return "Delete a cassette from your library.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Playlist());
    }

    private void deleteByName(CommandContext ctx) {
        // Concatenate args
        StringBuilder argBuilder = new StringBuilder();
        for (int i = 0; i < ctx.args().size(); i++) {
            if (i != 0) {
                argBuilder.append(" ");
            }

            argBuilder.append(ctx.args().get(i));
        }

        List<com.raikuman.troubleclub.radio.music.playlist.Playlist> playlists = new ArrayList<>();
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

        int iterator = 0;
        List<EmbedBuilder> pages = new ArrayList<>();
        for (com.raikuman.troubleclub.radio.music.playlist.Playlist playlist : playlists) {
            pages.add(EmbedResources.defaultResponse(
                    MusicManager.CASSETTE_COLOR,
                    "",
                    "",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor())
                .setTitle((iterator + 1) + ". " + playlist.getTitle())
                .addField("Songs in cassette", String.valueOf(playlist.getNumSongs()), true)
                .addField("Creator", playlist.getUser().getEffectiveName(), true)
                .addField("Id", String.valueOf(playlist.getId()), true));

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
}
