package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles renaming a user's playlist
 *
 * @version 1.0 2023-11-01
 * @since 1.2
 */
public class RenamePlaylist implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();

		// Check args
		if (ctx.getArgs().size() != 2) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		// Get playlist number
		int playlistNum;
		try {
			playlistNum = Integer.parseInt(ctx.getArgs().get(0));
		} catch (NumberFormatException e) {
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

		if (playlistName.length() > 20) {
			MessageResources.timedMessage(
				"You must provide a cassette name within 20 characters",
				channel,
				5
			);
			return;
		}

		// Rename playlist
		if (!PlaylistDB.renamePlaylist(playlistNum, ctx.getEventMember().getIdLong(), "Unnamed Cassette")) {
			MessageResources.timedMessage(
				"Could not rename your cassette: `" + playlistNum + "`",
				channel,
				5
			);
			return;
		}

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "renameplaylist";
	}

	@Override
	public String getUsage() {
		return "<playlist #> <playlist rename>";
	}

	@Override
	public String getDescription() {
		return "Renames a playlist";
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
