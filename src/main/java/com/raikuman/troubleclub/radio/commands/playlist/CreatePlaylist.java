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
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles creating a playlist from tracks from the queue, or a YouTube playlist link
 *
 * @version 1.7 2023-06-07
 * @since 1.2
 */
public class CreatePlaylist implements CommandInterface {

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

		if (!memberVoiceState.inAudioChannel() || (memberVoiceState.getGuild() != ctx.getGuild())) {

			if (ctx.getArgs().size() == 0 || !ctx.getArgs().get(0).contains("https://www.youtube.com/playlist?list=")) {
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

			if (ctx.getArgs().size() == 0 || !ctx.getArgs().get(0).contains("https://www.youtube.com/playlist?list=")) {
				MessageResources.timedMessage(
					"You must be in `" + selfVoiceState.getChannel().getName() + "` to use this command",
					channel,
					5
				);
				return;
			}
		}

		// Check for args
		if (ctx.getArgs().size() == 0) {
			createPlaylistFromQueue(ctx, "");
		} else if (ctx.getArgs().size() == 1) {
			// Check if arg is a link or a name
			if (ctx.getArgs().get(0).contains("https://www.youtube.com/playlist?list=")) {
				loadPlaylistInManager(ctx, ctx.getArgs().get(0), "");
			} else {
				createPlaylistFromQueue(ctx, ctx.getArgs().get(0));
			}
		} else if (ctx.getArgs().size() == 2) {
			// Check if arg is a link and a name
			if (ctx.getArgs().get(0).contains("https://www.youtube.com/playlist?list=") && !
				ctx.getArgs().get(1).contains("https://www.youtube.com/playlist?list=")) {
				loadPlaylistInManager(ctx, ctx.getArgs().get(0), ctx.getArgs().get(1));
			} else {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
			}
		} else {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
		}

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "createplaylist";
	}

	@Override
	public String getUsage() {
		return "(<YouTube playlist url>)";
	}

	@Override
	public String getDescription() {
		return "Creates a cassette given the current queue, or given a YouTube playlist link";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"cpl",
			"createcassette",
			"ccass"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new PlaylistCategory();
	}

	/**
	 * Creates a playlist with songs from the queue
	 * @param ctx The context to get the music manager and send messages with
	 * @param playlistName The name of the playlist to create
	 */
	private void createPlaylistFromQueue(CommandContext ctx, String playlistName) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		// Get music manager to retrieve tracks
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();

		// Retrieve tracks
		List<AudioTrack> tracks = new ArrayList<>();

		// Check for playing track
		if (audioPlayer.getPlayingTrack() != null) {
			tracks.add(audioPlayer.getPlayingTrack());
		}

		// Add all tracks from queue
		tracks.addAll(trackScheduler.queue);

		if (tracks.size() == 0) {
			MessageResources.timedMessage(
				"There's currently nothing in the queue to create a cassette",
				channel,
				5
			);
			return;
		}

		// Remove link from track url and leave id
		List<String> songIds = tracks
			.stream()
			.map(track -> track.getInfo().uri)
			.map(uri -> uri.replace("https://www.youtube.com/watch?v=", ""))
			.collect(Collectors.toList());

		// Check for playlist name
		if (playlistName.isEmpty()) {
			playlistName = "Unnamed Cassette";
		}

		// Create playlist
		PlaylistInfo playlistInfo = new PlaylistInfo(playlistName, songIds);
		boolean created = PlaylistDB.createPlaylist(playlistInfo, ctx.getEventMember().getUser());
		if (!created) {
			MessageResources.timedMessage(
				"An error occurred while creating your cassette",
				channel,
				5
			);
			return;
		}

		sendEmbed(playlistInfo, channel, ctx.getEventMember().getUser());
	}

	/**
	 * Calls PlayerManager instance to handle retrieving playlist for creating playlist
	 * @param ctx The context to manipulate messages with
	 * @param playlistLink The playlist link to create a playlist
	 * @param playlistName The playlist name
	 */
	private void loadPlaylistInManager(CommandContext ctx, String playlistLink, String playlistName) {
		// Get music manager to handle playlist
		PlayerManager.getInstance().handlePlaylist(ctx, playlistLink, new Triple<>(playlistName, 0, 0), false);
	}

	/**
	 * Creates a playlist with a link
	 * @param ctx The context to manipulate messages with
	 * @param audioPlaylist The AudioPlaylist from the playlist link
	 * @param playlistName The playlist name
	 */
	public void createPlaylistFromLink(CommandContext ctx, AudioPlaylist audioPlaylist, String playlistName) {
		final TextChannel channel = ctx.getChannel().asTextChannel();
		if (audioPlaylist == null) {
			MessageResources.timedMessage(
				"An error occurred while creating your cassette",
				channel,
				5
			);
			return;
		}

		// Remove link from track url and leave id
		List<String> songIds = audioPlaylist.getTracks()
			.stream()
			.map(track -> track.getInfo().uri)
			.map(uri -> uri.replace("https://www.youtube.com/watch?v=", ""))
			.collect(Collectors.toList());

		// Check for playlist name
		if (playlistName.isEmpty()) {
			playlistName = "Unnamed Cassette";
		}

		// Create playlist
		PlaylistInfo playlistInfo = new PlaylistInfo(playlistName, songIds);
		boolean created = PlaylistDB.createPlaylist(playlistInfo, ctx.getEventMember().getUser());
		if (!created) {
			MessageResources.timedMessage(
				"An error occurred while creating your cassette",
				channel,
				5
			);
			return;
		}

		sendEmbed(playlistInfo, channel, ctx.getEventMember().getUser());
	}

	/**
	 * Send an embed with the newly created playlist
	 * @param playlistInfo The PlaylistInfo to populate embed
	 * @param channel The channel to send embed
	 * @param user The user who created the playlist
	 */
	private void sendEmbed(PlaylistInfo playlistInfo, TextChannel channel, User user) {
		String songPlural = "song";
		if (playlistInfo.getSongs().size() > 1) {
			songPlural += "s";
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("\uD83D\uDCFC Creating cassette:", null, user.getEffectiveAvatarUrl())
			.setTitle(playlistInfo.getName())
			.addField("Songs in Cassette", "`" + playlistInfo.getSongs().size() + "` " + songPlural, true);

		channel.sendMessageEmbeds(builder.build())
			.delay(Duration.ofSeconds(7))
			.flatMap(Message::delete)
			.queue();
	}
}
