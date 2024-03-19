package com.raikuman.troubleclub.radio.music.musichandler;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public class PlayHandler extends MusicHandler {

    public PlayHandler(Guild guild, MessageChannelUnion channel, Message message, User user, String url) {
        super(guild, channel, message, user, url);
    }

    @Override
    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager) {
        return new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.getCurrentTrackScheduler().queue(audioTrack);
                getMessageChannel().sendMessageEmbeds(
                    getTrackLoadedEmbed(musicManager, audioTrack).build()
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
                                getMessageChannel(), getUser()));
                        return;
                    }

                    // Queue found track
                    musicManager.getCurrentTrackScheduler().queue(audioTrack);
                    getMessageChannel().sendMessageEmbeds(
                        getTrackLoadedEmbed(musicManager, audioTrack).build()
                    ).queue();
                } else {
                    // Queue playlist tracks
                    for (AudioTrack track : audioTracks) {
                        musicManager.getCurrentTrackScheduler().queue(track);
                    }

                    getMessageChannel().sendMessageEmbeds(
                        MusicManager.getPlaylistEmbed(musicManager, "▶️ Adding playlist to queue:",
                            getMessageChannel(), getUser(),
                            audioPlaylist.getName(), audioTracks, false).build()
                    ).queue();
                }

                getMessage().delete().queue();
            }

            @Override
            public void noMatches() {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music not found!", "Nothing found using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageResources.embedReplyDelete(getMessage(), 10, true,
                    EmbedResources.error("Music could not load!", "Could not load using `" + getUrl() + "`",
                        getMessageChannel(), getUser()));
            }
        };
    }

    private EmbedBuilder getTrackLoadedEmbed(GuildMusicManager musicManager, AudioTrack audioTrack) {
        String method;
        if (musicManager.getCurrentTrackScheduler().queue.isEmpty()) {
            method = "▶️ Playing:";
        } else {
            method = "↪️ Adding to queue:";
        }
        return MusicManager.getAudioTrackEmbed(musicManager, method, getMessageChannel(), getUser(),
            musicManager.getCurrentTrackScheduler().queue.size(), audioTrack);
    }
}
