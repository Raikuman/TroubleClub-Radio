package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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
 * Handles adding a song to a user's playlist
 *
 * @version 1.0 2023-06-07
 * @since 1.3
 */
public class AddSongToPlaylist implements CommandInterface {

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
        int playlistNum = -1, queueNum = -1, trackNum = -1;
        String songUrl = null;

        // Check if there are 2-3 args
        if (ctx.getArgs().size() >= 2 && ctx.getArgs().size() <= 3) {
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

            // Retrieve second arg, queueNum or songUrl
            String secondArg = ctx.getArgs().get(1);

            if (secondArg.contains("https://www.youtube.com/watch?v=")) {
                // Get song url
                songUrl = secondArg;
            } else {
                // Get queue num
                try {
                    queueNum = Integer.parseInt(ctx.getArgs().get(1));

                    final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
                    final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
                    final TrackScheduler trackScheduler = musicManager.getTrackScheduler();

                    if (queueNum == 0) {
                        if (audioPlayer.getPlayingTrack() != null) {
                            songUrl = audioPlayer.getPlayingTrack().getInfo().uri;
                        }
                    } else {
                        if (queueNum > trackScheduler.queue.size()) {
                            throw new NumberFormatException();
                        }
                        songUrl = ((AudioTrack) trackScheduler.queue.toArray()[queueNum - 1]).getInfo().uri;
                    }
                } catch (NumberFormatException e) {
                    MessageResources.timedMessage(
                        "You must provide a valid argument for this command: `" + getUsage() + "`",
                        channel,
                        5
                    );
                    return;
                }
            }

            // Retrieve 3rd arg
            if (ctx.getArgs().size() == 3) {
                try {
                    trackNum = Integer.parseInt(ctx.getArgs().get(2));
                } catch (NumberFormatException e) {
                    MessageResources.timedMessage(
                        "You must provide a valid argument for this command: `" + getUsage() + "`",
                        channel,
                        5
                    );
                    return;
                }
            }
        } else {
            MessageResources.timedMessage(
                "You must provide a valid argument for this command: `" + getUsage() + "`",
                channel,
                5
            );
            return;
        }

        if (!memberVoiceState.inAudioChannel() || (memberVoiceState.getGuild() != ctx.getGuild())) {
            if (queueNum != -1) {
                MessageResources.timedMessage(
                    "You must be in a voice channel to use this command",
                    channel,
                    5
                );
                return;
            }
        }

        if (selfVoiceState.inAudioChannel() && (selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
            if (selfVoiceState.getChannel() == null) {
                MessageResources.connectError(channel, 5);
                return;
            }

            if (queueNum != -1) {
                MessageResources.timedMessage(
                    "You must be in `" + selfVoiceState.getChannel().getName() + "` to use this command",
                    channel,
                    5
                );
                return;
            }
        }

        // Handle adding song to playlist
        if (songUrl == null) {
            MessageResources.timedMessage(
                "There was an error adding the song to your cassette",
                channel,
                5
            );
            return;
        }

        // Ensures song is added before user is prompted with embed
        boolean added = PlaylistDB.addSongToPlaylist(playlistNum, songUrl.replace("https://www.youtube.com/watch?v=", ""), trackNum);
        if (!added) {
            MessageResources.timedMessage(
                "There was an error adding the song to your cassette",
                channel,
                5
            );
            return;
        }

        loadSongInManager(ctx, songUrl, targetPlaylist);
    }

    /**
     * Calls PlayerManager instance to handle retrieving tracks for playlist
     * @param ctx The context to manipulate messages with
     * @param songLink The song to add to the playlist
     * @param playlist The playlist information
     */
    private void loadSongInManager(CommandContext ctx, String songLink, Triple<String, Integer, Integer> playlist) {
        // Get music manager to handle song
        PlayerManager.getInstance().handlePlaylist(ctx, songLink, playlist, true);
    }

    /**
     * Prompts user with embed where the song was added
     * @param ctx The context to manipulate messages with
     * @param playlist The playlist information
     * @param audioTrack The AudioTrack from the song link
     */
    public void addSongToPlaylist(CommandContext ctx, Triple<String, Integer, Integer> playlist,
                                  AudioTrack audioTrack) {
        // Send embed
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(RandomColor.getRandomColor())
            .setAuthor("\uD83D\uDCFC Adding song to cassette:", null, ctx.getEventMember().getEffectiveAvatarUrl())
            .setTitle(audioTrack.getInfo().title)
            .setDescription("Added to cassette **" + playlist.getFirst() + "**")
            .addField("Songs in Cassette", "`" + (playlist.getSecond() + 1) + "` songs", true);

        ctx.getChannel().sendMessageEmbeds(builder.build())
            .delay(Duration.ofSeconds(5))
            .flatMap(Message::delete)
            .queue();

        ctx.getEvent().getMessage().delete().queue();
    }

    @Override
    public String getUsage() {
        return "<cassette # or name> (<# of song in queue>) (<YouTube song url>) (<playlist track #>)";
    }

    @Override
    public String getDescription() {
        return "Add a song to a cassette. (Ex. `" + getInvoke() + " 1 4 3` places the 4th song in the queue to " +
            "cassette 1's track 3)";
    }

    @Override
    public String getInvoke() {
        return "addsong";
    }

    @Override
    public List<String> getAliases() {
        return List.of(
            "apl",
            "as",
            "acass"
        );
    }

    @Override
    public CategoryInterface getCategory() {
        return new PlaylistCategory();
    }
}
