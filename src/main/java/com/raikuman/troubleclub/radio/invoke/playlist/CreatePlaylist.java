package com.raikuman.troubleclub.radio.invoke.playlist;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.playlist.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.invoke.music.Play;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.raikuman.troubleclub.radio.music.musichandler.PlaylistCreatorHandler;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class CreatePlaylist extends Command {

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.args().isEmpty()) {
            // Playlist from queue with default name
            playlistFromQueue(ctx, "Playlist #" + (PlaylistDatabaseHandler.numberOfPlaylists(ctx.event().getAuthor()) + 1));
        } else if (ctx.args().size() == 1) {
            // Check for url
            if (Play.isUrl(ctx.args().get(0)) && ctx.args().get(0).contains("youtube.com/playlist?list")) {
                // Playlist from link with default name
                MusicManager.getInstance().play(new PlaylistCreatorHandler(
                    ctx.event().getGuild(),
                    ctx.event().getChannel(),
                    ctx.event().getMessage(),
                    ctx.event().getAuthor(),
                    ctx.args().get(0),
                    ""));
            } else {
                // Playlist from queue with custom name
                if (ctx.args().get(0).length() > 20 || ctx.args().get(0).length() < 3) {
                    playlistNameTooLong(ctx);
                } else {
                    playlistFromQueue(ctx, ctx.args().get(0));
                }
            }
        } else {
            // Last arg will always be a link, so concatenate all args before it
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 0; i < ctx.args().size() - 1; i++) {
                if (i != 0) {
                    nameBuilder.append(" ");
                }

                nameBuilder.append(ctx.args().get(i));
            }

            if (nameBuilder.toString().length() > 20 || nameBuilder.toString().length() < 3) {
                playlistNameTooLong(ctx);
            } else {
                String link = ctx.args().get(ctx.args().size() - 1);
                if (Play.isUrl(link) && link.contains("youtube.com/playlist?list")) {
                    // Playlist from link with custom name
                    MusicManager.getInstance().play(new PlaylistCreatorHandler(
                        ctx.event().getGuild(),
                        ctx.event().getChannel(),
                        ctx.event().getMessage(),
                        ctx.event().getAuthor(),
                        link,
                        nameBuilder.toString()));
                }
            }
        }
    }

    @Override
    public String getInvoke() {
        return "createcassette";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cc", "createc");
    }

    @Override
    public String getUsage() {
        return "(<cassette name>) (<playlist link>)";
    }

    @Override
    public String getDescription() {
        return "Create a cassette from the current track's playing song and queue, or from a playlist with a link.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Playlist());
    }

    private void playlistFromQueue(CommandContext ctx, String name) {
        if (MusicChecking.setup(
                ctx.event().getGuild(),
                ctx.event().getChannel(),
                ctx.event().getMessage(),
                ctx.event().getMember())
            .checkMemberNotInVoiceChannel(true)
            .checkBotInDifferentVoiceChannel(true, true)
            .check()) {
            return;
        }

        // Retrieve all songs in track
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();
        List<AudioTrack> audioTracks = new ArrayList<>();
        if (trackScheduler.audioPlayer.getPlayingTrack() != null) {
            audioTracks.add(trackScheduler.audioPlayer.getPlayingTrack());
        }
        audioTracks.addAll(trackScheduler.queue);

        if (audioTracks.isEmpty()) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.error(
                    "No songs to create a cassette with!",
                    "There must be a song playing or songs in queue to create a cassette.",
                    ctx.event().getChannel(), ctx.event().getAuthor()));
        }

        // Encode all songs
        List<String> encodedTracks = new ArrayList<>();
        long playlistLength = 0L;
        for (AudioTrack audioTrack : audioTracks) {
            encodedTracks.add(musicManager.encodeTrack(audioTrack));
            playlistLength += audioTrack.getDuration();
        }

        boolean playlistCreated = PlaylistDatabaseHandler.addPlaylist(
            new com.raikuman.troubleclub.radio.music.playlist.Playlist(
                name,
                encodedTracks.size(),
                ctx.event().getAuthor()),
            encodedTracks);

        if (playlistCreated) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                PlaylistUtils.getPlaylistInfoEmbed(
                    ctx.event().getChannel(),
                    ctx.event().getAuthor(),
                    "\uD83D\uDCFC Created Cassette!",
                    name,
                    encodedTracks.size(),
                    playlistLength
                ));

            ctx.event().getMessage().delete().queue();
        } else {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error("Could not create cassette!", "Could not add cassette to database.",
                    ctx.event().getChannel(), ctx.event().getAuthor()));
        }
    }


    private void playlistNameTooLong(CommandContext ctx) {
        MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
            EmbedResources.error("The naming convention of the cassette is incorrect!",
                "Names must be within 3-20 characters.",
                ctx.event().getChannel(),
                ctx.event().getAuthor()));
    }
}
