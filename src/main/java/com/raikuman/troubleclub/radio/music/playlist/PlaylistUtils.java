package com.raikuman.troubleclub.radio.music.playlist;

import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.troubleclub.radio.invoke.playlist.CreatePlaylist;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.ArrayList;
import java.util.List;

public class PlaylistUtils {

    public static int getPlaylistId(MessageEmbed embed) {
        int playlistId = 0;
        for (MessageEmbed.Field field : embed.getFields()) {
            if (field.getName() != null && field.getName().equals("Id") && field.getValue() != null) {
                try {
                    playlistId = Integer.parseInt(field.getValue());
                } catch (NumberFormatException e) {
                    playlistId = -1;
                }
                break;
            }
        }

        return playlistId;
    }

    public static List<EmbedBuilder> getPlaylistPages(List<Playlist> playlists, MessageChannelUnion channel, User user) {
        List<EmbedBuilder> pages = new ArrayList<>();
        for (com.raikuman.troubleclub.radio.music.playlist.Playlist playlist : playlists) {
            pages.add(getPlaylistInfoEmbed(
                channel,
                user,
                "",
                playlist.getTitle(),
                playlist.getNumSongs(),
                0L));
        }

        return pages;
    }

    public static EmbedBuilder getPlaylistInfoEmbed(MessageChannelUnion channelUnion, User user, String title, String playlistName, int numSongs, long playlistLength) {
        EmbedBuilder embedBuilder = EmbedResources.defaultResponse(
                MusicManager.CASSETTE_COLOR,
                title,
                "",
                channelUnion,
                user)
            .setTitle(playlistName)
            .addField("Songs in cassette", String.valueOf(numSongs), true);

        if (playlistLength > 0L) {
            embedBuilder.addField("Length of cassette", MusicManager.formatMilliseconds(playlistLength), true);
        }

        return embedBuilder;
    }
}
