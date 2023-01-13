package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.commands.music.Join;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

/**
 * Handles playing a playlist to the queue
 *
 * @version 1.2 2023-13-01
 * @since 1.2
 */
public class PlayPlaylist implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		new Join().joinChannel(ctx);

		// Check args
		if (ctx.getArgs().size() != 1) {
			MessageResources.timedMessage(
				"You must provide a cassette to play",
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

		// Get playlist to play from database
		PlaylistInfo info = PlaylistDB.getPlaylist(ctx.getEventMember().getIdLong(), playlistNum);
		if (info == null) {
			MessageResources.timedMessage(
				"Could not find your cassette: `" + playlistNum + "`",
				channel,
				5
			);
			return;
		}

		PlayerManager.getInstance().loadFromDatabase(channel, info, ctx.getEvent().getAuthor(),
			ctx.getGuild().getIdLong());

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "playplaylist";
	}

	@Override
	public String getUsage() {
		return "<playlist #>";
	}

	@Override
	public String getDescription() {
		return "Plays the selected playlist";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"ppl",
			"playcassette",
			"pcass"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new PlaylistCategory();
	}
}
