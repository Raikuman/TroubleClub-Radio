package com.raikuman.troubleclub.radio.music.playerhandler.music;

import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class PlayHandler extends MusicHandler {

    public PlayHandler(MessageReceivedEvent event, String url) {
        super(event, url);
    }

    @Override
    public AudioLoadResultHandler getResultHandler() {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                getMusicManager().getCurrentTrackScheduler().queue(audioTrack);
                getChannel().sendMessageEmbeds(
                    getTrackLoadedEmbed(audioTrack).build()
                ).queue();

                getMessage().delete().queue();
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
                                getChannel(), getUser()));
                        return;
                    }

                    // Queue found track
                    getMusicManager().getCurrentTrackScheduler().queue(audioTrack);
                    getChannel().sendMessageEmbeds(
                        getTrackLoadedEmbed(audioTrack).build()
                    ).queue();
                } else {
                    // Queue playlist tracks
                    for (AudioTrack track : audioTracks) {
                        getMusicManager().getCurrentTrackScheduler().queue(track);
                    }

                    getChannel().sendMessageEmbeds(
                        MusicManager.getPlaylistEmbed(getMusicManager(), "▶️ Adding playlist to queue:",
                            getChannel(), getUser(),
                            audioPlaylist.getName(), audioTracks, false).build()
                    ).queue();
                }

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

    private EmbedBuilder getTrackLoadedEmbed(AudioTrack audioTrack) {
        String method;
        if (getMusicManager().getCurrentTrackScheduler().queue.isEmpty()) {
            method = "▶️ Playing:";
        } else {
            method = "↪️ Adding to queue:";
        }
        return MusicManager.getAudioTrackEmbed(getMusicManager(), method, getChannel(), getUser(),
            getMusicManager().getCurrentTrackScheduler().queue.size(), audioTrack);
    }
}
