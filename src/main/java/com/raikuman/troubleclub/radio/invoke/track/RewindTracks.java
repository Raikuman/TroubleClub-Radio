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
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public class RewindTracks extends Command {

    @Override
    public void handle(CommandContext ctx) {
        if (MusicChecking.setup(
                ctx.event().getGuild(),
                ctx.event().getChannel(),
                ctx.event().getMessage(),
                ctx.event().getMember())
            .checkMemberNotInVoiceChannel(true)
            .checkBotInDifferentVoiceChannel(true, true)
            .check()) {
            return;
        }

        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        StringBuilder rewindBuilder = new StringBuilder();
        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            AudioPlayer audioPlayer = musicManager.getAudioPlayer(i);
            AudioTrack currentTrack = audioPlayer.getPlayingTrack();

            if (i != 1) {
                rewindBuilder.append("\n\n");
            }

            rewindBuilder
                .append("**Track ")
                .append(i)
                .append("**\n");

            if (currentTrack != null) {
                audioPlayer.startTrack(currentTrack.makeClone(), false);

                rewindBuilder
                    .append("⏪ *Rewinding: `")
                    .append(currentTrack.getInfo().title)
                    .append("`*");
            } else {
                rewindBuilder
                    .append("*Nothing to rewind.*");
            }
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            EmbedResources.defaultResponse(
                MusicManager.TRACK_COLOR,
                "⏪ Rewinding Tracks",
                rewindBuilder.toString(),
                ctx.event().getChannel(),
                ctx.event().getAuthor()));

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "rewindtracks";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rewindt");
    }

    @Override
    public String getDescription() {
        return "Rewinds all tracks' playing song to the beginning.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Track());
    }
}
