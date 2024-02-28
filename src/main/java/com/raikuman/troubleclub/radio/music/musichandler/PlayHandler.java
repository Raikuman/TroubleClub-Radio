package com.raikuman.troubleclub.radio.music.musichandler;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public class PlayHandler extends MusicHandler {

    public PlayHandler(CommandContext ctx, String url) {
        super(ctx, url);
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
                // Handle playlist
                List<AudioTrack> audioTracks = audioPlaylist.getTracks();

                if (getUrl().contains("ytsearch:")) {
                    // Handle searching songs
                    AudioTrack audioTrack = audioTracks.get(0);
                    if (audioTrack == null) {
                        MessageResources.embedReplyDelete(getMessage(), 10, true,
                            EmbedResources.error("Could not find track!", "Nothing found using `" + getUrl() + "`",
                                getMessageChannel(), getUser()));
                        return;
                    }

                    // Queue found track
                    handleTrackLoaded(musicManager, audioTrack);
                } else {
                    // Queue playlist tracks
                    for (AudioTrack track : audioTracks) {
                        musicManager.getTrackScheduler().queue(track);
                    }

                    MusicManager.addPlaylist("▶️ Adding playlist to queue:", getMessageChannel(), getUser(),
                        audioPlaylist.getName(), audioTracks, false);
                }
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music not found!", "Nothing found using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music could not load!", "Could not load using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }
        };
    }

    private void handleTrackLoaded(GuildMusicManager musicManager, AudioTrack audioTrack) {
        musicManager.getTrackScheduler().queue(audioTrack);
        String method;
        if (musicManager.getTrackScheduler().queue.isEmpty()) {
            method = "▶️ Playing:";
        } else {
            method = "↪️ Adding to queue:";
        }
        MusicManager.addAudioTrack(method, getMessageChannel(), getUser(),
            musicManager.getTrackScheduler().queue.size(), audioTrack);
    }
}
