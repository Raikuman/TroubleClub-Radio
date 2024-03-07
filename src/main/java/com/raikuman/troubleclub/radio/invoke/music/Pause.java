package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class Pause extends Command {

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
        AudioPlayer audioPlayer = musicManager.getCurrentAudioPlayer();
        AudioTrack currentTrack = audioPlayer.getPlayingTrack();
        if (currentTrack != null) {
            if (audioPlayer.isPaused()) {
                audioPlayer.setPaused(false);

                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    resumingSong(musicManager, ctx));
            } else {
                audioPlayer.setPaused(true);

                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    pausingSong(musicManager, ctx));
            }
        } else {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                noSong(musicManager, ctx, "⏸ No song to pause!"));
        }

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "pause";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pa");
    }

    @Override
    public String getDescription() {
        return "Pauses the current track's playing song, or resumes if there is a paused song.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    private EmbedBuilder pausingSong(GuildMusicManager musicManager, CommandContext ctx) {
        return NowPlaying.songInfoEmbed(
                ctx,
                musicManager.getCurrentAudioPlayer().getPlayingTrack(),
                musicManager.getCurrentAudioPlayerNum())
            .setAuthor("⏸ Pausing song:", null, ctx.event().getAuthor().getEffectiveAvatarUrl());
    }

    public static EmbedBuilder resumingSong(GuildMusicManager musicManager, CommandContext ctx) {
        return NowPlaying.songInfoEmbed(
                ctx,
                musicManager.getCurrentAudioPlayer().getPlayingTrack(),
                musicManager.getCurrentAudioPlayerNum())
            .setAuthor("▶️ Resuming song:", null, ctx.event().getAuthor().getEffectiveAvatarUrl());
    }

    public static EmbedBuilder noSong(GuildMusicManager musicManager, CommandContext ctx, String title) {
        return EmbedResources.defaultResponse(
                MusicManager.MUSIC_COLOR,
                title,
                "",
                ctx.event().getChannel(),
                ctx.event().getAuthor())
            .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName());
    }
}
