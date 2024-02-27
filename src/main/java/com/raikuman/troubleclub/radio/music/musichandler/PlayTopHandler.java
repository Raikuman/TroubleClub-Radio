package com.raikuman.troubleclub.radio.music.musichandler;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class PlayTopHandler extends MusicHandler {

    private boolean playNow = false;

    public PlayTopHandler(CommandContext ctx, String url, boolean playNow) {
        super(ctx, url);
        this.playNow = playNow;
    }

    @Override
    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager) {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                handleTrackLoaded(musicManager, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                handlePlaylistLoaded(musicManager, audioPlaylist.getName(), audioPlaylist.getTracks(), false);
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10,
                    EmbedResources.error("Music not found!", "Nothing found using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10,
                    EmbedResources.error("Music could not load!", "Could not load using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }
        };
    }

    @Override
    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager, Playlist playlist) {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                handlePlaylistLoaded(musicManager, playlist.title(), List.of(audioTrack), true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                handlePlaylistLoaded(musicManager, playlist.title(), audioPlaylist.getTracks(), true);
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10,
                    EmbedResources.error("Cassette not found!", "Nothing found from `" + playlist.title() + "`",
                        getMessageChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10,
                    EmbedResources.error("Cassette could not load!", "Could not load using `" + playlist.title() + "`",
                        getMessageChannel(), getUser()));
            }
        };
    }

    private void handleTrackLoaded(GuildMusicManager musicManager, AudioTrack audioTrack) {
        TrackScheduler trackScheduler = musicManager.getTrackScheduler();

        // Drain tracks to new queue
        List<AudioTrack> queueTracks = new ArrayList<>();
        trackScheduler.queue.drainTo(queueTracks);

        // Add track to top of queue
        trackScheduler.queue(audioTrack);

        // Add the rest of the tracks to queue
        for (AudioTrack queueTrack : queueTracks) {
            trackScheduler.queue.offer(queueTrack);
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
        MusicManager.addAudioTrack(method, getMessageChannel(), getUser(),
            musicManager.getTrackScheduler().queue.size(), audioTrack);
    }

    private void handlePlaylistLoaded(GuildMusicManager musicManager, String playlistName,
                                      List<AudioTrack> playlistTracks, boolean isCassette) {
        TrackScheduler trackScheduler = musicManager.getTrackScheduler();

        // Drain tracks to new queue
        List<AudioTrack> queueTracks = new ArrayList<>();
        trackScheduler.queue.drainTo(queueTracks);

        // Add tracks to top of the queue
        for (AudioTrack audioTrack : playlistTracks) {
            trackScheduler.queue.offer(audioTrack);
        }

        // Add the rest of the tracks to queue
        for (AudioTrack queueTrack : queueTracks) {
            trackScheduler.queue.offer(queueTrack);
        }

        // Play result immediately if playNow is true
        if (playNow && trackScheduler.audioPlayer.getPlayingTrack() != null) {
            trackScheduler.nextTrack();
        }

        String method;
        if (isCassette) {
            if (playNow) {
                method = "\uD83D\uDCFC▶️ Playing cassette at top of queue:";
            } else {
                method = "\uD83D\uDCFC↪️ Adding cassette to top of queue:";
            }
        } else {
            if (playNow) {
                method = "▶️ Playing playlist at top of queue:";
            } else {
                method = "⬆️↪️ Adding playlist to top of queue:";
            }
        }

        MusicManager.addPlaylist(method, getMessageChannel(),
            getUser(), playlistName, playlistTracks, isCassette);
    }
}
