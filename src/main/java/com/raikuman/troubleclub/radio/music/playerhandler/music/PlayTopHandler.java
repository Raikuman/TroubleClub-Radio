package com.raikuman.troubleclub.radio.music.playerhandler.music;

import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayTopHandler extends MusicHandler {

    private final boolean playNow;

    public PlayTopHandler(MessageReceivedEvent event, String url) {
        super(event, url);
        this.playNow = false;
    }

    public PlayTopHandler(MessageReceivedEvent event, String url, boolean playNow) {
        super(event, url);
        this.playNow = playNow;
    }

    @Override
    public AudioLoadResultHandler getResultHandler() {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                TrackScheduler trackScheduler = getMusicManager().getCurrentTrackScheduler();

                // Drain tracks to new queue
                List<AudioTrack> queueTracks = new ArrayList<>();
                trackScheduler.queue.drainTo(queueTracks);

                // Add track to top of queue
                trackScheduler.queue(audioTrack);

                // Add the rest of the tracks to queue
                for (AudioTrack queueTrack : queueTracks) {
                    trackScheduler.queue(queueTrack);
                }

                // Play result immediately if playNow is true
                if (playNow && trackScheduler.audioPlayer.getPlayingTrack() != null) {
                    trackScheduler.nextTrack();
                }

                String method;
                if (playNow) {
                    method = "▶️ Playing:";
                } else {
                    method = "⬆️️ Adding to queue:";
                }

                getChannel().sendMessageEmbeds(
                    MusicManager.getAudioTrackEmbed(getMusicManager(), method, getChannel(), getUser(),
                        getMusicManager().getCurrentTrackScheduler().queue.size(), audioTrack).build()
                ).queue();

                getMessage().delete().queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> playlistTracks = audioPlaylist.getTracks();
                TrackScheduler trackScheduler = getMusicManager().getCurrentTrackScheduler();

                // Drain tracks to new queue
                List<AudioTrack> queueTracks = new ArrayList<>();
                trackScheduler.queue.drainTo(queueTracks);

                // Add tracks to top of the queue
                for (AudioTrack audioTrack : playlistTracks) {
                    trackScheduler.queue(audioTrack);
                }

                // Add the rest of the tracks to queue
                for (AudioTrack queueTrack : queueTracks) {
                    trackScheduler.queue(queueTrack);
                }

                // Play result immediately if playNow is true
                if (playNow && trackScheduler.audioPlayer.getPlayingTrack() != null) {
                    trackScheduler.nextTrack();
                }

                String method;
                if (playNow) {
                    method = "▶️ Playing playlist at top of queue:";
                } else {
                    method = "⬆️↪️ Adding playlist to top of queue:";
                }

                getChannel().sendMessageEmbeds(
                    MusicManager.getPlaylistEmbed(getMusicManager(), method, getChannel(),
                        getUser(), audioPlaylist.getName(), playlistTracks, false).build()
                ).queue();

                getMessage().delete().queue();
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music not found!", "Nothing found using `" + getUrl() + "`",
                        getChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music could not load!", "Could not load using `" + getUrl() + "`",
                        getChannel(), getUser()));
            }
        };
    }
}
