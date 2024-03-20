package com.raikuman.troubleclub.radio.music.playerhandler.playlist;

import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

import java.util.Collections;

public class PlayShufflePlaylistHandler extends PlaylistHandler {

    public PlayShufflePlaylistHandler(StringSelectInteraction interaction, Playlist playlist) {
        super(interaction, playlist);
    }

    @Override
    public void playPlaylist() {
        if (nullAudioTracks()) {
            return;
        }

        Collections.shuffle(getAudioTracks());
        for (AudioTrack track : getAudioTracks()) {
            getMusicManager().getCurrentTrackScheduler().queue(track);
        }

        getInteraction().editMessageEmbeds(
                MusicManager.getPlaylistEmbed(
                    getMusicManager(),
                    "\uD83D\uDCFC\uD83D\uDD00 Shuffled and adding cassette to queue:",
                    getInteraction().getChannel(),
                    getUser(), getPlaylist().getTitle(), getAudioTracks(), true).build())
            .setComponents()
            .queue();
    }
}
