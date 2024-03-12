package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.musichandler.PlayShuffleTopHandler;

import java.util.List;

public class PlayShuffleTop extends Command {

    @Override
    public void handle(CommandContext ctx) {
        // Check if member is in a voice channel
        if (MusicChecking.isMemberNotInVoiceChannel(ctx)) {
            return;
        }

        // Check if bot is in a voice channel that is not the member's
        if (MusicChecking.isBotInDifferentVoiceChannel(ctx, false)) {
            return;
        }

        // Check if the bot has permissions to join
        if (MusicChecking.lacksPermissionToJoin(ctx)) {
            return;
        }

        if (!ctx.args().isEmpty()) {
            // Connect
            MusicManager.getInstance().connect(ctx);

            String link = String.join(" ", ctx.args());
            if (!Play.isUrl(link)) {
                link = "ytsearch:" + link;
            }

            MusicManager.getInstance().play(new PlayShuffleTopHandler(ctx, link));
        } else {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            ctx.event().getMessage().delete().queue();
        }
    }

    @Override
    public String getInvoke() {
        return "playshuffletop";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pst", "playst");
    }

    @Override
    public String getUsage() {
        return "<link/search>";
    }

    @Override
    public String getDescription() {
        return "Plays a song or playlist from a link, or search for a song to shuffle and queue to the top of the current track's queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}