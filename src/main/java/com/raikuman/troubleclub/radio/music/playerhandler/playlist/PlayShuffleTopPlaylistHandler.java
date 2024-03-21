package com.raikuman.troubleclub.radio.music.playerhandler.playlist;

import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayShuffleTopPlaylistHandler extends PlaylistHandler {

    private final boolean playNow;

    public PlayShuffleTopPlaylistHandler(StringSelectInteraction interaction, Playlist playlist) {
        super(interaction, playlist);
        this.playNow = false;
    }

    public PlayShuffleTopPlaylistHandler(StringSelectInteraction interaction, Playlist playlist, boolean playNow) {
        super(interaction, playlist);
        this.playNow = playNow;
    }

    @Override
    public void playPlaylist() {
        if (nullAudioTracks()) {
            return;
        }

        TrackScheduler trackScheduler = getMusicManager().getCurrentTrackScheduler();

        // Drain tracks to new queue
        List<AudioTrack> queueTracks = new ArrayList<>();
        trackScheduler.queue.drainTo(queueTracks);

        Collections.shuffle(getAudioTracks());

        // Add tracks to top of the queue
        for (AudioTrack audioTrack : getAudioTracks()) {
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
            method = "\uD83D\uDCFC\uD83D\uDD00▶️ Shuffled and playing cassette at top of queue:";
        } else {
            method = "\uD83D\uDCFC\uD83D\uDD00⬆️ Shuffled and adding cassette to top of queue:";
        }

        getInteraction().editMessageEmbeds(
                MusicManager.getPlaylistEmbed(getMusicManager(), method, getChannel(),
                    getUser(), getPlaylist().getTitle(), getAudioTracks(), true).build())
            .setComponents()
            .queue();
    }
}
