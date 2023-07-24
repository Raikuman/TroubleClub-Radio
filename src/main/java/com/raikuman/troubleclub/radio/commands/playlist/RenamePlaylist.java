package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles renaming a user's playlist
 *
 * @version 1.4 2023-24-07
 * @since 1.2
 */
public class RenamePlaylist implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		// Check args
		if (ctx.getArgs().size() < 1) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		List<Triple<String, Integer, Integer>> playlists = PlaylistDB.getBasicPlaylistInfo(ctx.getEventMember().getUser());
		// Get playlist number
		int playlistNum = -1;
		try {
			playlistNum = Integer.parseInt(ctx.getArgs().get(0));
			if (playlistNum > playlists.size()) {
				playlistNum = -1;
			}
		} catch (NumberFormatException e) {
			for (int i = 0; i < playlists.size(); i++) {
				if (ctx.getArgs().get(0).equalsIgnoreCase(playlists.get(i).getFirst())) {
					playlistNum = i + 1;
				}
			}
		}

		if (playlistNum == -1) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		// Handle getting name of playlist
		String playlistName;
		if (ctx.getArgs().size() == 1)
			playlistName = "";
		else {
			playlistName = ctx.getArgs().stream().skip(1).collect(Collectors.joining(" "));
		}

		if (playlistName.length() > 20 || playlistName.length() == 0) {
			MessageResources.timedMessage(
				"You must provide a cassette name within 20 characters",
				channel,
				5
			);
			return;
		}



		// Rename playlist
		if (!PlaylistDB.renamePlaylist(playlists.get(playlistNum - 1).getThird(), playlistName)) {
			MessageResources.timedMessage(
				"Could not rename your cassette: `" + playlistNum + "`",
				channel,
				5
			);
			return;
		}

		// Send rename embed
		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("\uD83D\uDCFC Renamed Cassette #" + playlistNum + ": " + playlists.get(playlistNum - 1).getFirst()
					+ " to " + playlistName, null,
				ctx.getEventMember().getEffectiveAvatarUrl());

		ctx.getChannel().sendMessageEmbeds(builder.build())
			.delay(Duration.ofSeconds(7))
			.flatMap(Message::delete)
			.queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "renameplaylist";
	}

	@Override
	public String getUsage() {
		return "<playlist #> (<playlist rename>)";
	}

	@Override
	public String getDescription() {
		return "Renames a playlist. If given no arguments, the playlist will be called \"Unnamed Cassette\"";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"rpl",
			"renamecassette",
			"rcass"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new PlaylistCategory();
	}
}
