package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

/**
 * Handles shuffling and playing a playlist to the queue
 *
 * @version 1.2 2023-29-06
 * @since 1.2
 */
public class ShufflePlayPlaylist implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		PlaylistInfo info = new PlayPlaylist().handlePlaylist(ctx, channel);
		if (info == null)
			return;

		PlayerManager.getInstance().loadFromDatabase(channel, info,
			ctx.getGuild(), true);

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "shuffleplayplaylist";
	}

	@Override
	public String getUsage() {
		return "<playlist #>";
	}

	@Override
	public String getDescription() {
		return "Shuffles and plays the selected playlist";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"shuffleplaypl",
			"splayplaylist",
			"splaypl",
			"sppl",
			"spp",
			"shuffleplaycassette",
			"shuffleplaycass",
			"splaycassette",
			"splaycass",
			"spcass",
			"spc"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new PlaylistCategory();
	}
}
