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
import net.dv8tion.jda.api.EmbedBuilder;

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
                List<AudioTrack> audioTracks = audioPlaylist.getTracks();
                Collections.shuffle(audioTracks);

                // Queue playlist tracks
                for (AudioTrack track : audioTracks) {
                    musicManager.getCurrentTrackScheduler().queue(track);
                }

                getMessageChannel().sendMessageEmbeds(
                    getPlaylistLoadedEmbed(audioPlaylist.getName(), audioPlaylist.getTracks(), false).build()
                ).queue();

                getMessage().delete().queue();
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
                musicManager.getCurrentTrackScheduler().queue(audioTrack);

                getMessageChannel().sendMessageEmbeds(
                    getPlaylistLoadedEmbed(playlist.getTitle(), List.of(audioTrack), true).build()
                ).queue();

                getMessage().delete().queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> audioTracks = audioPlaylist.getTracks();
                Collections.shuffle(audioTracks);

                // Queue playlist tracks
                for (AudioTrack track : audioTracks) {
                    musicManager.getCurrentTrackScheduler().queue(track);
                }

                getMessageChannel().sendMessageEmbeds(
                    getPlaylistLoadedEmbed(playlist.getTitle(), audioPlaylist.getTracks(), true).build()
                ).queue();

                getMessage().delete().queue();
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

    private EmbedBuilder getPlaylistLoadedEmbed(String playlistName, List<AudioTrack> playlistTracks, boolean isCassette) {
        String method;
        if (isCassette) {
            method = "\uD83D\uDCFC\uD83D\uDD00 Shuffled and adding cassette to queue:";
        } else {
            method = "\uD83D\uDD00↪️ Shuffled and adding playlist to queue:";
        }

        return MusicManager.getPlaylistEmbed(method, getMessageChannel(),
            getUser(), playlistName, playlistTracks, isCassette);
    }
}
