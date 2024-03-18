package com.raikuman.troubleclub.radio.music.musichandler;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.playlist.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.invoke.playlist.CreatePlaylist;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCreatorHandler extends MusicHandler {

    private final String name;

    public PlaylistCreatorHandler(CommandContext ctx, String url, String name) {
        super(ctx, url);
        this.name = name;
    }

    @Override
    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager) {
        return new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error(
                        "No playlist found!",
                        "You must provide a valid playlist link.",
                        getMessageChannel(),
                        getUser()));
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
                    MessageResources.embedDelete(getMessageChannel(), 10,
                        CreatePlaylist.getCassetteInfoEmbed(
                            getMessageChannel(),
                            getUser(),
                            "\uD83D\uDCFC Created Cassette!",
                            newName,
                            encodedTracks.size(),
                            playlistLength
                        ));

                    getMessage().delete().queue();
                } else {
                    MessageResources.embedReplyDelete(getMessage(), 10, true,
                        EmbedResources.error("Could not create cassette!", "Could not add cassette to database.",
                            getMessageChannel(), getUser()));
                }
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
                    EmbedResources.error("Playlist not found!", "Nothing found using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }
        };
    }
}
