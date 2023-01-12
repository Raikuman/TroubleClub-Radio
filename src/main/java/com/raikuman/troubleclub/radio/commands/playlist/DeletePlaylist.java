package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

/**
 * Handles deleting a playlist from a user's playlist collection
 *
 * @version 1.1 2023-11-01
 * @since 1.2
 */
public class DeletePlaylist implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		// Check args
		if (ctx.getArgs().size() != 1) {
			MessageResources.timedMessage(
				"You must provide a cassette to delete",
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

		// Delete playlist
		if (!PlaylistDB.deletePlaylist(playlistNum, ctx.getEventMember().getIdLong())) {
			MessageResources.timedMessage(
				"Could not delete your cassette: `" + playlistNum + "`",
				channel,
				5
			);
			return;
		}

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "deleteplaylist";
	}

	@Override
	public String getUsage() {
		return "<playlist #>";
	}

	@Override
	public String getDescription() {
		return "Deletes a playlist";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"dpl",
			"deletecassette",
			"dcass"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new PlaylistCategory();
	}
}
