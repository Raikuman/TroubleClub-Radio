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
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class ResumeTracks extends Command {

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
        int numResumed = 0;
        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            AudioPlayer audioPlayer = musicManager.getAudioPlayer(i);

            if (audioPlayer.getPlayingTrack() != null && audioPlayer.isPaused()) {
                audioPlayer.setPaused(false);
                numResumed++;
            }
        }

        if (numResumed > 0) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                resumingTracks(ctx, numResumed));
        } else {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                noTracks(ctx));
        }

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "resumetracks";
    }

    @Override
    public List<String> getAliases() {
        return List.of("resumet");
    }

    @Override
    public String getDescription() {
        return "Resumes all paused tracks.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Track());
    }

    private EmbedBuilder resumingTracks(CommandContext ctx, int numPaused) {
        String title = "▶️ ";
        if (numPaused == GuildMusicManager.MAX_AUDIO_PLAYERS) {
            title += "All";
        } else {
            title += numPaused;
        }
        title += " paused ";
        if (numPaused == 1) {
            title += "track";
        } else {
            title += "tracks";
        }
        title += " have been resumed!";

        return EmbedResources.defaultResponse(
            MusicManager.TRACK_COLOR,
            title,
            "",
            ctx.event().getChannel(),
            ctx.event().getAuthor());
    }

    private EmbedBuilder noTracks(CommandContext ctx) {
        return EmbedResources.defaultResponse(
            MusicManager.TRACK_COLOR,
            "▶️ All tracks have no song to resume!",
            "",
            ctx.event().getChannel(),
            ctx.event().getAuthor());
    }
}
