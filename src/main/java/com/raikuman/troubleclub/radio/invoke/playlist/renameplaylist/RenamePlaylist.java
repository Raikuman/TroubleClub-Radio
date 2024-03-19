package com.raikuman.troubleclub.radio.invoke.playlist.renameplaylist;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.pagination.Pagination;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.playlist.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class RenamePlaylist extends Command {

    @Override
    public void handle(CommandContext ctx) {
        List<com.raikuman.troubleclub.radio.music.playlist.Playlist> playlists =
            PlaylistDatabaseHandler.getPlaylists(ctx.event().getAuthor());

        if (playlists == null) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error(
                    "There was an error getting your cassettes!",
                    "Could not retrieve cassettes from the database.",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));
            return;
        }

        List<EmbedBuilder> pages = PlaylistUtils.getPlaylistPages(
            playlists, ctx.event().getChannel(), ctx.event().getAuthor());

        int iterator = 0;
        for (EmbedBuilder page : pages) {
            page.addField("Id", String.valueOf(playlists.get(iterator).getId()), true);
            iterator++;
        }

        new Pagination(
            ctx.event().getAuthor(),
            "\uD83D\uDCFC Rename a Cassette",
            ((messageChannelUnion, user) -> pages),
            componentHandler)
            .setExtraButtons(new RenamePlaylistButton())
            .setHasFirstPage(true)
            .setLooping(true)
            .sendPagination(ctx);
    }

    @Override
    public String getInvoke() {
        return "renamecassette";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rc", "renamec");
    }

    @Override
    public String getUsage() {
        return "<cassette #/name> <new name of playlist>";
    }

    @Override
    public String getDescription() {
        return "Rename a cassette from your library.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Playlist());
    }
}
