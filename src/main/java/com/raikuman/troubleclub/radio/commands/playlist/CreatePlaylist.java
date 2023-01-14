package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles creating a playlist from tracks from the queue, or a YouTube playlist link
 *
 * @version 1.4 2023-13-01
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

		if (!memberVoiceState.inAudioChannel()) {
			MessageResources.timedMessage(
				"You must be in a voice channel to use this command",
				channel,
				5
			);
			return;
		}

		if (selfVoiceState.inAudioChannel() && (selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
			if (selfVoiceState.getChannel() == null) {
				MessageResources.connectError(channel, 5);
				return;
			}

			MessageResources.timedMessage(
				"You must be in `" + selfVoiceState.getChannel().getName() + "` to use this command",
				channel,
				5
			);
			return;
		}

		if (ctx.getArgs().size() == 0) {
			playlistFromQueue(ctx, "");
		} else if (ctx.getArgs().size() > 0) {
			if (ctx.getArgs().get(0).contains("https://www.youtube.com/playlist?list=")) {
				if (ctx.getArgs().size() == 1) {
					playlistFromArg(ctx, ctx.getArgs().get(0), "");
				} else {
					playlistFromArg(ctx, ctx.getArgs().get(0),
						ctx.getArgs().stream().skip(1).collect(Collectors.joining(" ")));
				}
			} else {
				playlistFromQueue(ctx, String.join(" ", ctx.getArgs()));
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
		return "(<youtube playlist url>)";
	}

	@Override
	public String getDescription() {
		return "Creates a playlist given the current queue, or given a playlist link";
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

	private void playlistFromQueue(CommandContext ctx, String playlistName) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();

		List<AudioTrack> tracks = new ArrayList<>();

		if (audioPlayer.getPlayingTrack() != null)
			tracks.add(audioPlayer.getPlayingTrack());

		tracks.addAll(trackScheduler.queue);

		if (tracks.size() == 0) {
			MessageResources.timedMessage(
				"There's currently nothing in the queue to create a cassette",
				channel,
				5
			);
			return;
		}

		List<String> songUrls = tracks
			.stream()
			.map(track -> track.getInfo().uri)
			.map(uri -> uri.replace("https://www.youtube.com/watch?v=", ""))
			.collect(Collectors.toList());

		int playlistState = PlaylistDB.createPlaylistQueue(new PlaylistInfo(playlistName, songUrls.size(),
			songUrls, ctx.getEventMember().getIdLong()));

		if (playlistState > 0) {
			MessageResources.timedMessage(
				"An error occurred while creating your playlist with error code `" + playlistState + "`",
				channel,
				5
			);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("\uD83D\uDCFC Creating cassette:", null, ctx.getEventMember().getEffectiveAvatarUrl())
			.addField("Songs in Cassette", "`" + tracks.size() + "` songs", true);

		if (playlistName.isEmpty())
			builder.setTitle("Unnamed Cassette");
		else
			builder.setTitle(playlistName);

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
	}

	private void playlistFromArg(CommandContext ctx, String arg, String playlistName) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		if (!arg.contains("https://www.youtube.com/playlist?list=")) {
			MessageResources.timedMessage(
				"You must provide a valid YouTube playlist link to create a cassette this way",
				channel,
				5
			);
			return;
		}

		String playlistLink = arg.replace("https://www.youtube.com/playlist?list=", "");

		int playlistState = PlaylistDB.createPlaylistLink(new PlaylistInfo(playlistName, playlistLink,
			ctx.getEventMember().getIdLong()));

		if (playlistState > 0) {
			MessageResources.timedMessage(
				"An error occurred while creating your playlist with error code `" + playlistState + "`",
				channel,
				5
			);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("\uD83D\uDCFC Creating cassette:", null, ctx.getEventMember().getEffectiveAvatarUrl())
			.addField("▶️YouTube: ", "`" + arg + "`", true);

		if (playlistName.isEmpty())
			builder.setTitle("Unnamed Cassette");
		else
			builder.setTitle(playlistName);

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
	}
}
