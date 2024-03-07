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
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class Random extends Command {

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
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();

        if (!trackScheduler.queue.isEmpty()) {
            // Play random track from queue
            List<AudioTrack> queueTracks = new ArrayList<>();
            trackScheduler.queue.drainTo(queueTracks);

            int position = new java.util.Random().nextInt(queueTracks.size());
            AudioTrack newTrack = queueTracks.remove(position);

            trackScheduler.queue.offer(newTrack);
            for (AudioTrack audioTrack : queueTracks) {
                trackScheduler.queue.offer(audioTrack);
            }

            trackScheduler.nextTrack();

            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                NowPlaying.songInfoEmbed(ctx, trackScheduler.audioPlayer.getPlayingTrack(), musicManager.getCurrentAudioPlayerNum())
                    .setAuthor("\uD83C\uDFB2 Randomly playing a new song:", null, ctx.event().getAuthor().getEffectiveAvatarUrl()));
        } else {
            // Nothing in queue
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.error("Cannot randomly play a song!", "There must be songs in the queue to play a random song.",
                        ctx.event().getChannel(), ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        }

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "random";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rand");
    }

    @Override
    public String getDescription() {
        return "Plays a random song from the current track's queue and skips the current song.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
