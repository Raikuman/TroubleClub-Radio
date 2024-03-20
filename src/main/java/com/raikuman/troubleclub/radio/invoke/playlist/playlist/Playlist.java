package com.raikuman.troubleclub.radio.invoke.playlist.playlist;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.pagination.Pagination;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.playlist.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class Playlist extends Command {

    @Override
    public void handle(CommandContext ctx) {
        // Check args and mentions
        List<Member> mentions = ctx.event().getMessage().getMentions().getMembers();
        if (ctx.args().size() > 1 || mentions.size() > 1) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            return;
        }

        // Retrieve user's playlists
        String targetString;
        User targetUser = ctx.event().getAuthor();
        boolean notSelf = false;
        if (!mentions.isEmpty() && !ctx.event().getAuthor().equals(mentions.get(0))) {
            targetUser = mentions.get(0).getUser();
            notSelf = true;
        }
        List<com.raikuman.troubleclub.radio.music.playlist.Playlist> playlists = PlaylistDatabaseHandler.getPlaylists(targetUser);

        // Handle empty playlists/errors
        if (playlists == null) {
            targetString = "your";
            if (notSelf) {
                targetString = mentions.get(0).getEffectiveName() + "'s";
            }

            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error(
                    "There was an error getting " + targetString + " cassettes!",
                    "Could not retrieve cassettes from the database.",
                    ctx.event().getChannel(),
                    targetUser));
            return;
        } else if (playlists.isEmpty()) {
            targetString = "You do";
            if (notSelf) {
                targetString = mentions.get(0).getEffectiveName() + " does";
            }

            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                    MusicManager.CASSETTE_COLOR,
                    "\uD83D\uDCFC " + targetString + " not have any cassettes!",
                    "",
                    ctx.event().getChannel(),
                    targetUser));
            ctx.event().getMessage().delete().queue();
            return;
        }

        targetString = "Your Cassettes";
        if (notSelf) {
            targetString = targetUser.getEffectiveName() + "'s Cassettes";
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
            "\uD83D\uDCFC " + targetString,
            ((messageChannelUnion, user) -> pages),
            componentHandler)
            .setSelectMenu("Play Cassette", List.of(
                new PlaySelect(),
                new PlayTopSelect(),
                new PlayNowSelect(),
                new PlayShuffleSelect(),
                new PlayShuffleTopSelect(),
                new PlayShuffleTopNowSelect()))
            .setLooping(true)
            .setHasFirstPage(true)
            .sendPagination(ctx);
    }

    @Override
    public String getInvoke() {
        return "cassette";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cas", "cass");
    }

    @Override
    public String getUsage() {
        return "(<@user>)";
    }

    @Override
    public String getDescription() {
        return "Show a library of your cassettes, or a user's cassettes.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new com.raikuman.troubleclub.radio.invoke.category.Playlist());
    }
}
