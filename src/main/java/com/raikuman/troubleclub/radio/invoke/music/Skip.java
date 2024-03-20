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

import java.util.List;

public class Skip extends Command {

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

        skipToNextTrack(ctx);
    }

    @Override
    public String getInvoke() {
        return "skip";
    }

    @Override
    public List<String> getAliases() {
        return List.of("s");
    }

    @Override
    public String getDescription() {
        return "Skips the current track's playing song to the next song in queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    public static void skipToNextTrack(CommandContext ctx) {
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        AudioPlayer audioPlayer = musicManager.getCurrentAudioPlayer();
        AudioTrack currentTrack = audioPlayer.getPlayingTrack();
        if (currentTrack != null) {
            musicManager.getCurrentTrackScheduler().nextTrack();
            if (!musicManager.getCurrentTrackScheduler().queue.isEmpty()) {
                currentTrack = audioPlayer.getPlayingTrack();

                // Skip to next track
                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    NowPlaying.songInfoEmbed(ctx, currentTrack, musicManager.getCurrentAudioPlayerNum())
                        .setAuthor("⏭ Playing next song:", null, ctx.event().getAuthor().getEffectiveAvatarUrl()));
            } else {
                // Nothing to skip to
                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    EmbedResources.defaultResponse(
                        MusicManager.MUSIC_COLOR,
                        "⏭ Skipped song to empty queue.",
                        "",
                        ctx.event().getChannel(),
                        ctx.event().getAuthor())
                        .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
            }
        } else {
            // No track is currently playing
            MessageResources.embedDelete(
                ctx.event().getChannel(),
                10,
                EmbedResources.error("No song is currently playing!", "A song must be playing to be skipped.",
                        ctx.event().getChannel(), ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName())
            );
        }

        ctx.event().getMessage().delete().queue();
    }
}
