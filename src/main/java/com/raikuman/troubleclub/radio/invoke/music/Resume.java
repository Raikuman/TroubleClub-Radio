package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public class Resume extends Command {

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
        AudioPlayer audioPlayer = musicManager.getCurrentAudioPlayer();
        AudioTrack currentTrack = audioPlayer.getPlayingTrack();
        if (currentTrack != null) {
            if (audioPlayer.isPaused()) {
                audioPlayer.setPaused(false);

                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    Pause.resumingSong(musicManager, ctx));
            } else {
                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    Play.songAlreadyPlaying(ctx));
            }
        } else {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                Pause.noSong(musicManager, ctx, "▶️ No song to resume!"));
        }
    }

    @Override
    public String getInvoke() {
        return "resume";
    }

    @Override
    public String getDescription() {
        return "Resumes the current track's paused song.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
