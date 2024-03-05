package com.raikuman.troubleclub.radio.music.musichandler;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Collections;
import java.util.List;

public class PlayShuffleHandler extends MusicHandler {

    public PlayShuffleHandler(CommandContext ctx, String url) {
        super(ctx, url);
    }

    @Override
    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager) {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Playlist could not load!", "Could not load using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                handlePlaylistLoaded(musicManager, audioPlaylist.getName(), audioPlaylist.getTracks(), false);
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Playlist not found!", "Nothing found using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Playlist could not load!", "Could not load using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }
        };
    }

    @Override
    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager, Playlist playlist) {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                handlePlaylistLoaded(musicManager, playlist.getTitle(), List.of(audioTrack), true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                handlePlaylistLoaded(musicManager, playlist.getTitle(), audioPlaylist.getTracks(), true);
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Cassette not found!", "Nothing found from `" + playlist.getTitle() + "`",
                        getMessageChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Cassette could not load!", "Could not load using `" + playlist.getTitle() + "`",
                        getMessageChannel(), getUser()));
            }
        };
    }

    private void handlePlaylistLoaded(GuildMusicManager musicManager, String playlistName,
                                      List<AudioTrack> playlistTracks, boolean isCassette) {
        Collections.shuffle(playlistTracks);

        // Queue playlist tracks
        for (AudioTrack track : playlistTracks) {
            musicManager.getTrackScheduler().queue(track);
        }

        String method;
        if (isCassette) {
            method = "\uD83D\uDCFC\uD83D\uDD00 Shuffled and adding cassette to queue:";
        } else {
            method = "\uD83D\uDD00↪️ Shuffled and adding playlist to queue:";
        }

        MusicManager.addPlaylist(method, getMessageChannel(),
            getUser(), playlistName, playlistTracks, false);
    }
}
