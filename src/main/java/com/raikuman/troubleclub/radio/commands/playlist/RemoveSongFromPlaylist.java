package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Duration;
import java.util.List;

/**
 * Handles removing a song from a user's playlist
 *
 * @version 1.0 2023-06-07
 * @since 1.3
 */
public class RemoveSongFromPlaylist implements CommandInterface {

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel().asTextChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (selfVoiceState == null) {
            MessageResources.connectError(channel, 5);
            return;
        }

        GuildVoiceState memberVoiceState = ctx.getEventMember().getVoiceState();
        if (memberVoiceState == null) {
            MessageResources.connectError(channel, 5);
            return;
        }

        List<Triple<String, Integer, Integer>> playlists = PlaylistDB.getBasicPlaylistInfo(ctx.getEventMember().getUser());
        Triple<String, Integer, Integer> targetPlaylist = null;
        int playlistNum = -1, songNum = -1;

        if (ctx.getArgs().size() == 2) {
            // Retrieve first arg, playlistNum
            try {
                playlistNum = Integer.parseInt(ctx.getArgs().get(0));
                if (playlistNum > playlists.size()) {
                    playlistNum = -1;
                }
                targetPlaylist = playlists.get(playlistNum - 1);
                playlistNum = targetPlaylist.getThird();
            } catch (NumberFormatException e) {
                for (Triple<String, Integer, Integer> playlist : playlists) {
                    if (ctx.getArgs().get(0).equalsIgnoreCase(playlist.getFirst())) {
                        playlistNum = playlist.getThird();
                        targetPlaylist = playlist;
                        break;
                    }
                }
            }

            if (playlistNum == -1 || targetPlaylist == null) {
                MessageResources.timedMessage(
                    "You must provide a valid argument for this command: `" + getUsage() + "`",
                    channel,
                    5
                );
                return;
            }

            // Retrieve second arg, songNum
            try {
                songNum = Integer.parseInt(ctx.getArgs().get(1));

                if (songNum > targetPlaylist.getSecond()) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                MessageResources.timedMessage(
                    "You must provide a valid argument for this command: `" + getUsage() + "`",
                    channel,
                    5
                );
                return;
            }
        } else {
            MessageResources.timedMessage(
                "You must provide a valid argument for this command: `" + getUsage() + "`",
                channel,
                5
            );
            return;
        }

        // Handle removing song from user playlist
        String removedSong = PlaylistDB.removeSongFromPlaylist(playlistNum, songNum);
        if (removedSong.isEmpty()) {
            MessageResources.timedMessage(
                "There was an error removing the song to your cassette",
                channel,
                5
            );
            return;
        }

        loadSongInManager(ctx, removedSong, targetPlaylist);
    }

    /**
     * Calls PlayerManager instance to handle retrieving tracks for playlist
     * @param ctx The context to manipulate messages with
     * @param songLink The song to remove from the playlist
     * @param playlist The playlist information
     */
    private void loadSongInManager(CommandContext ctx, String songLink, Triple<String, Integer, Integer> playlist) {
        // Get music manager to handle song
        PlayerManager.getInstance().handlePlaylist(ctx, songLink, playlist, false);
    }

    /**
     * Prompts user with embed where the song was added
     * @param ctx The context to manipulate messages with
     * @param playlist The playlist information
     * @param audioTrack The AudioTrack from the song link
     */
    public void removeSongFromPlaylist(CommandContext ctx, Triple<String, Integer, Integer> playlist, AudioTrack audioTrack) {
        String songPlural = "song";
        if (playlist.getSecond() - 1 == 1) {
            songPlural += "s";
        }

        // Send embed
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(RandomColor.getRandomColor())
            .setAuthor("\uD83D\uDCFC Removing song from cassette:", null, ctx.getEventMember().getEffectiveAvatarUrl())
            .setTitle(audioTrack.getInfo().title)
            .setDescription("Removed from cassette **" + playlist.getFirst() + "**")
            .addField("Songs in Cassette", "`" + (playlist.getSecond() - 1) + "` " + songPlural, true);

        ctx.getChannel().sendMessageEmbeds(builder.build())
            .delay(Duration.ofSeconds(5))
            .flatMap(Message::delete)
            .queue();

        ctx.getEvent().getMessage().delete().queue();
    }

    @Override
    public String getUsage() {
        return "<cassette # or name> <# of song in cassette>";
    }

    @Override
    public String getDescription() {
        return "Remove a song from a cassette. (Ex. `" + getInvoke() + " 1 4` removes the 4th song from cassette 1)";
    }

    @Override
    public List<String> getAliases() {
        return List.of(
            "rpl",
            "rs",
            "rcass"
        );
    }

    @Override
    public String getInvoke() {
        return "removesong";
    }

    @Override
    public CategoryInterface getCategory() {
        return new PlaylistCategory();
    }
}
