package com.raikuman.troubleclub.radio.music.playerhandler.music;

import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;

public class PlayShuffleHandler extends MusicHandler {

    public PlayShuffleHandler(MessageReceivedEvent event, String url) {
        super(event, url);
    }

    @Override
    public AudioLoadResultHandler getResultHandler() {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                getMusicManager().getCurrentTrackScheduler().queue(audioTrack);

                String method;
                if (getMusicManager().getCurrentTrackScheduler().queue.isEmpty()) {
                    method = "▶️ Playing:";
                } else {
                    method = "↪️ Adding to queue:";
                }

                getChannel().sendMessageEmbeds(
                    MusicManager.getAudioTrackEmbed(getMusicManager(), method, getChannel(), getUser(),
                        getMusicManager().getCurrentTrackScheduler().queue.size(), audioTrack).build()
                ).queue();

                getMessage().delete().queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> audioTracks = audioPlaylist.getTracks();
                Collections.shuffle(audioTracks);

                // Queue playlist tracks
                for (AudioTrack track : audioTracks) {
                    getMusicManager().getCurrentTrackScheduler().queue(track);
                }

                getChannel().sendMessageEmbeds(
                    getPlaylistLoadedEmbed(audioPlaylist.getName(), audioPlaylist.getTracks()).build()
                ).queue();

                getMessage().delete().queue();
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Playlist not found!", "Nothing found using `" + getUrl() + "`",
                        getChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Playlist could not load!", "Could not load using `" + getUrl() + "`",
                        getChannel(), getUser()));
            }
        };
    }

//    @Override
//    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager, Playlist playlist) {
//        return new AudioLoadResultHandler() {
//
//            @Override
//            public void trackLoaded(AudioTrack audioTrack) {
//                musicManager.getCurrentTrackScheduler().queue(audioTrack);
//
//                getMessageChannel().sendMessageEmbeds(
//                    getPlaylistLoadedEmbed(musicManager, playlist.getTitle(), List.of(audioTrack), true).build()
//                ).queue();
//
//                getMessage().delete().queue();
//            }
//
//            @Override
//            public void playlistLoaded(AudioPlaylist audioPlaylist) {
//                List<AudioTrack> audioTracks = audioPlaylist.getTracks();
//                Collections.shuffle(audioTracks);
//
//                // Queue playlist tracks
//                for (AudioTrack track : audioTracks) {
//                    musicManager.getCurrentTrackScheduler().queue(track);
//                }
//
//                getMessageChannel().sendMessageEmbeds(
//                    getPlaylistLoadedEmbed(musicManager, playlist.getTitle(), audioPlaylist.getTracks(), true).build()
//                ).queue();
//
//                getMessage().delete().queue();
//            }
//
//            @Override
//            public void noMatches() {
//                MessageResources.embedReplyDelete(getMessage(), 10, true,
//                    EmbedResources.error("Cassette not found!", "Nothing found from `" + playlist.getTitle() + "`",
//                        getMessageChannel(), getUser()));
//            }
//
//            @Override
//            public void loadFailed(FriendlyException e) {
//                MessageResources.embedReplyDelete(getMessage(), 10, true,
//                    EmbedResources.error("Cassette could not load!", "Could not load using `" + playlist.getTitle() + "`",
//                        getMessageChannel(), getUser()));
//            }
//        };
//    }

    private EmbedBuilder getPlaylistLoadedEmbed(String playlistName, List<AudioTrack> playlistTracks) {
        return MusicManager.getPlaylistEmbed(
            getMusicManager(),
            "\uD83D\uDD00↪️ Shuffled and adding playlist to queue:",
            getChannel(),
            getUser(), playlistName, playlistTracks, false);
    }
}
