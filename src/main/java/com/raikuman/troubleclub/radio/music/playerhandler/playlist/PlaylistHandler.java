package com.raikuman.troubleclub.radio.music.playerhandler.playlist;

import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.troubleclub.radio.database.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaylistHandler {

    private final StringSelectInteraction interaction;
    private final GuildMusicManager musicManager;
    private final Playlist playlist;
    private final List<AudioTrack> audioTracks;

    public PlaylistHandler(StringSelectInteraction interaction, Playlist playlist) {
        this.interaction = interaction;
        this.playlist = playlist;

        Guild guild = interaction.getGuild();
        if (guild == null) {
            audioTracks = null;
            musicManager = null;
            return;
        }

        this.musicManager = MusicManager.getInstance().getMusicManager(guild);
        List<String> encodedTracks = PlaylistDatabaseHandler.getPlaylistTracks(playlist.getId());
        if (encodedTracks == null) {
            audioTracks = null;
            return;
        }

        List<AudioTrack> decodedTracks = new ArrayList<>();
        for (String encoded : encodedTracks) {
            AudioTrack decoded = musicManager.decodeTrack(encoded);
            if (decoded == null) {
                audioTracks = null;
                return;
            }

            decodedTracks.add(decoded);
        }
        audioTracks = decodedTracks;
    }

    public User getUser() {
        return interaction.getUser();
    }

    public StringSelectInteraction getInteraction() {
        return interaction;
    }

    public Guild getGuild() {
        return interaction.getGuild();
    }

    public MessageChannelUnion getChannel() {
        return interaction.getChannel();
    }

    public GuildMusicManager getMusicManager() {
        return musicManager;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public List<AudioTrack> getAudioTracks() {
        return audioTracks;
    }

    public abstract void playPlaylist();

    public boolean nullAudioTracks() {
        if (audioTracks == null) {
            interaction.editMessageEmbeds(
                EmbedResources.error(
                    "Could not play your cassette!",
                    "An error occurred when trying to retrieve your cassette.",
                    interaction.getChannel(),
                    interaction.getUser()).build())
                .setComponents()
                .delay(Duration.ofSeconds(10))
                .flatMap(InteractionHook::deleteOriginal)
                .queue();
            return true;
        }

        return false;
    }
}
