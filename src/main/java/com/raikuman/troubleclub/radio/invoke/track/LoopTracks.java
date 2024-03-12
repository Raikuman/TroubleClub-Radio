package com.raikuman.troubleclub.radio.invoke.track;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Track;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;

import java.util.List;

public class LoopTracks extends Command {

    @Override
    public void handle(CommandContext ctx) {
        // Check if member is in a voice channel
        if (MusicChecking.isMemberNotInVoiceChannel(ctx)) {
            return;
        }

        // Check if bot is in a voice channel that is not the member's
        if (MusicChecking.isBotInDifferentVoiceChannel(ctx, true)) {
            return;
        }

        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        boolean currentRepeating = musicManager.getCurrentTrackScheduler().isRepeatQueue();
        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            musicManager.getTrackScheduler(i).setRepeatQueue(!currentRepeating);
        }

        String title;
        if (currentRepeating) {
            title = "\uD83C\uDFBC\uD83D\uDD01 Stopped looping queue on all tracks!";
        } else {
            title = "\uD83C\uDFBC\uD83D\uDD01 Looping queue on all tracks!";
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            EmbedResources.defaultResponse(
                MusicManager.TRACK_COLOR,
                title,
                "",
                ctx.event().getChannel(),
                ctx.event().getAuthor()));

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "looptracks";
    }

    @Override
    public List<String> getAliases() {
        return List.of("lt", "loopt");
    }

    @Override
    public String getDescription() {
        return "Loops all tracks' queues. ";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Track());
    }
}
