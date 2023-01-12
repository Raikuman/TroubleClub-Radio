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
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles creating a playlist from tracks from the queue, or a YouTube playlist link
 *
 * @version 1.1 2023-11-01
 * @since 1.2
 */
public class CreatePlaylist implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
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

		String playlistName;
		if (ctx.getArgs().size() == 0)
			playlistName = "";
		else
			playlistName = String.join("_", ctx.getArgs());

		if (playlistName.length() > 20) {
			MessageResources.timedMessage(
				"You must provide a cassette name within 20 characters",
				channel,
				5
			);
			return;
		}

		int playlistState = PlaylistDB.createPlaylist(new PlaylistInfo(playlistName, songUrls.size(),
			songUrls, ctx.getEventMember().getIdLong()));

		if (playlistState == 1 || playlistState == 2) {
			MessageResources.timedMessage(
				"An error occured while creating your playlist with error code `" + playlistState + "`",
				channel,
				5
			);
		} else {
			EmbedBuilder builder = new EmbedBuilder()
				.setColor(RandomColor.getRandomColor())
				.setAuthor("\uD83D\uDCFC Creating cassette:", null, ctx.getEventMember().getEffectiveAvatarUrl())
				.addField("Songs in Cassette", "`" + tracks.size() + "` songs", true);

			if (playlistName.isEmpty())
				builder.setTitle("Unnamed Cassette");
			else
				builder.setTitle(playlistName.replace("_", " "));

			ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

			ctx.getEvent().getMessage().delete().queue();
		}
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
}
