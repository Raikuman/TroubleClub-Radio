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

public class Tracks extends Command {

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
        StringBuilder trackBuilder = new StringBuilder();
        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            if (i != 1) {
                trackBuilder.append("\n\n");
            }

            trackBuilder
                .append("**Track ")
                .append(i)
                .append("**\n");

            trackBuilder.append(
                com.raikuman.troubleclub.radio.invoke.track.Track.trackString(musicManager.getTrackScheduler(i)));
        }

        // Send track embed
        MessageResources.embedDelete(ctx.event().getChannel(), 30,
            EmbedResources.defaultResponse(
                MusicManager.TRACK_COLOR,
                "\uD83C\uDFBC Track Information",
                trackBuilder.toString(),
                ctx.event().getChannel(),
                ctx.event().getAuthor()));

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "tracks";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ts");
    }

    @Override
    public String getDescription() {
        return "Check the status of all tracks.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Track());
    }
}
