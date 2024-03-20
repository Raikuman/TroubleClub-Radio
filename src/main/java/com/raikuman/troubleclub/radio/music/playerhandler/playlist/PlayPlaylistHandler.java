package com.raikuman.troubleclub.radio.music.playerhandler.playlist;

import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

public class PlayPlaylistHandler extends PlaylistHandler {
    public PlayPlaylistHandler(StringSelectInteraction interaction, Playlist playlist) {
        super(interaction, playlist);
    }

    @Override
    public void playPlaylist() {
        if (nullAudioTracks()) {
            return;
        }

        for (AudioTrack track : getAudioTracks()) {
            getMusicManager().getCurrentTrackScheduler().queue(track);
        }

        String method;
        if (getMusicManager().getCurrentTrackScheduler().queue.isEmpty()) {
            method = "\uD83D\uDCFC▶️ Playing cassette:";
        } else {
            method = "\uD83D\uDCFC↪️ Adding cassette to queue:";
        }

        getInteraction().editMessageEmbeds(
                MusicManager.getPlaylistEmbed(getMusicManager(), method, getInteraction().getChannel(),
                    getUser(), getPlaylist().getTitle(), getAudioTracks(), true).build())
            .setComponents()
            .queue();
    }
}
