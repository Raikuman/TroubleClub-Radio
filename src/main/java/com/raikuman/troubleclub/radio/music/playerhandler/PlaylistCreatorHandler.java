package com.raikuman.troubleclub.radio.music.playerhandler;

import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PlaylistCreatorHandler {

    private final ModalInteractionEvent event;
    private final String url, name;

    public PlaylistCreatorHandler(ModalInteractionEvent event, String url, String name) {
        this.event = event;
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public MessageChannelUnion getChannel() {
        return event.getChannel();
    }

    public User getUser() {
        return event.getUser();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    private void closeEvent() {
        event.deferEdit().queue();
    }

    public AudioLoadResultHandler getResultHandler() {
        return new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error(
                        "No playlist found!",
                        "You must provide a valid playlist link.",
                        getChannel(), getUser()));
                closeEvent();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                // Retrieve all songs in track
                GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(getGuild());

                // Encode all songs
                List<String> encodedTracks = new ArrayList<>();
                long playlistLength = 0L;
                for (AudioTrack audioTrack : audioPlaylist.getTracks()) {
                    encodedTracks.add(musicManager.encodeTrack(audioTrack));
                    playlistLength += audioTrack.getDuration();
                }

                String newName = name;
                if (newName.isEmpty()) {
                    newName = audioPlaylist.getName();
                }

                boolean playlistCreated = PlaylistDatabaseHandler.addPlaylist(
                    new com.raikuman.troubleclub.radio.music.playlist.Playlist(
                        newName,
                        encodedTracks.size(),
                        getUser()),
                    encodedTracks);

                if (playlistCreated) {
                    getMessage().editMessageEmbeds(PlaylistUtils.getPlaylistInfoEmbed(
                        getChannel(),
                        getUser(),
                        "\uD83D\uDCFC Created Cassette!",
                        newName,
                        encodedTracks.size(),
                        playlistLength
                    ).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
                    closeEvent();
                } else {
                    getMessage().editMessageEmbeds(EmbedResources.error(
                        "Could not create cassette!",
                        "Could not add cassette to database.",
                        getChannel(), getUser()
                    ).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
                    closeEvent();
                }
            }

            @Override
            public void noMatches() {
                getMessage().editMessageEmbeds(EmbedResources.error(
                    "Playlist not found!",
                    "Nothing found using `" + url + "`",
                    getChannel(), getUser()
                ).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
                closeEvent();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                getMessage().editMessageEmbeds(EmbedResources.error(
                    "Playlist not found!",
                    "Nothing found using `" + url + "`",
                    getChannel(), getUser()
                ).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
                closeEvent();
            }
        };
    }
}
