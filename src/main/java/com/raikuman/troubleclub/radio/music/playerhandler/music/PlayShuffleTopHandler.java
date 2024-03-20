package com.raikuman.troubleclub.radio.music.playerhandler.music;

import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayShuffleTopHandler extends MusicHandler {

    private final boolean playNow;

    public PlayShuffleTopHandler(MessageReceivedEvent event, String url) {
        super(event, url);
        this.playNow = false;
    }

    public PlayShuffleTopHandler(MessageReceivedEvent event, String url, boolean playNow) {
        super(event, url);
        this.playNow = playNow;
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
                shuffleAndQueueToTop(audioTracks);

                getChannel().sendMessageEmbeds(
                    getPlaylistLoadedEmbed(getMusicManager(), audioPlaylist.getName(), audioTracks, false).build()
                ).queue();

                getMessage().delete().queue();
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music not found!", "Nothing found using `" + getUrl() + "`",
                        getChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music could not load!", "Could not load using `" + getUrl() + "`",
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
//                List<AudioTrack> audioTracks = new ArrayList<>();
//                audioTracks.add(audioTrack);
//                shuffleAndQueueToTop(musicManager, audioTracks);
//
//                getMessageChannel().sendMessageEmbeds(
//                    getPlaylistLoadedEmbed(musicManager, playlist.getTitle(), audioTracks, true).build()
//                ).queue();
//
//                getMessage().delete().queue();
//            }
//
//            @Override
//            public void playlistLoaded(AudioPlaylist audioPlaylist) {
//                List<AudioTrack> audioTracks = audioPlaylist.getTracks();
//                shuffleAndQueueToTop(musicManager, audioTracks);
//
//                getMessageChannel().sendMessageEmbeds(
//                    getPlaylistLoadedEmbed(musicManager, playlist.getTitle(), audioTracks, true).build()
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

    private void shuffleAndQueueToTop(List<AudioTrack> playlistTracks) {
        TrackScheduler trackScheduler = getMusicManager().getCurrentTrackScheduler();

        // Drain tracks to new queue
        List<AudioTrack> queueTracks = new ArrayList<>();
        trackScheduler.queue.drainTo(queueTracks);

        Collections.shuffle(playlistTracks);

        // Add tracks to top of the queue
        for (AudioTrack audioTrack : playlistTracks) {
            trackScheduler.queue.offer(audioTrack);
        }

        // Add the rest of the tracks to queue
        for (AudioTrack queueTrack : queueTracks) {
            trackScheduler.queue.offer(queueTrack);
        }

        // Play result immediately if playNow is true
        if (playNow && trackScheduler.audioPlayer.getPlayingTrack() != null) {
            trackScheduler.nextTrack();
        }
    }

    private EmbedBuilder getPlaylistLoadedEmbed(GuildMusicManager musicManager, String playlistName,
                                                List<AudioTrack> playlistTracks, boolean isCassette) {
        String method;
        if (isCassette) {
            if (playNow) {
                method = "\uD83D\uDCFC\uD83D\uDD00▶️ Shuffled and playing cassette at top of queue:";
            } else {
                method = "\uD83D\uDCFC\uD83D\uDD00⬆️ Shuffled and adding cassette to top of queue:";
            }
        } else {
            if (playNow) {
                method = "\uD83D\uDD00▶️ Shuffled and playing playlist at top of queue:";
            } else {
                method = "\uD83D\uDD00⬆️ Shuffled and adding playlist to top of queue:";
            }
        }

        return MusicManager.getPlaylistEmbed(musicManager, method, getChannel(),
            getUser(), playlistName, playlistTracks, isCassette);
    }
}
