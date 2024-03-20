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

public class PlayShuffleTopNow extends Command {

    @Override
    public void handle(CommandContext ctx) {
        if (MusicChecking.setup(
                ctx.event().getGuild(),
                ctx.event().getChannel(),
                ctx.event().getMessage(),
                ctx.event().getMember())
            .checkMemberNotInVoiceChannel(true)
            .checkBotInDifferentVoiceChannel(true, false)
            .checkLacksPermission(true)
            .check()) {
            return;
        }

        if (!ctx.args().isEmpty()) {
            // Connect
            MusicManager.getInstance().connect(ctx);

            String link = String.join(" ", ctx.args());
            if (!Play.isUrl(link)) {
                link = "ytsearch:" + link;
            }

            MusicManager.getInstance().play(new PlayShuffleTopHandler(
                ctx.event().getGuild(),
                ctx.event().getChannel(),
                ctx.event().getMessage(),
                ctx.event().getAuthor(),
                link,
                true));
        } else {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            ctx.event().getMessage().delete().queue();
        }
    }

    @Override
    public String getInvoke() {
        return "playshuffletopnow";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pstn", "playstn");
    }

    @Override
    public String getUsage() {
        return "<link/search>";
    }

    @Override
    public String getDescription() {
        return "Plays a song or playlist from a link, or search for a song to shuffle and play immediately on the current track.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
