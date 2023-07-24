package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
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
 * @version 1.6 2023-29-06
 * @since 1.2
 */
public class PlayPlaylist implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		PlaylistInfo info = handlePlaylist(ctx, channel);
		if (info == null)
			return;

		PlayerManager.getInstance().loadFromDatabase(channel, info,
			ctx.getGuild(), false);

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

	/**
	 * Handle getting playlist info from the database
	 * @param ctx The command context to get args and info from
	 * @param channel The channel to send messages to
	 * @return The PlaylistInfo object
	 */
	public PlaylistInfo handlePlaylist(CommandContext ctx, TextChannel channel) {
		if (!new Join().joinChannel(ctx)) {
			return null;
		}

		// Check args
		if (ctx.getArgs().size() != 1) {
			MessageResources.timedMessage(
				"You must provide a cassette to play",
				channel,
				5
			);
			return null;
		}

		// Get playlist number
		int playlistNum;
		try {
			playlistNum = Integer.parseInt(ctx.getArgs().get(0));
			if (playlistNum > PlaylistDB.getBasicPlaylistInfo(ctx.getEventMember().getUser()).size()) {
				playlistNum = -1;
			}
		} catch (NumberFormatException e) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return null;
		}

		if (playlistNum == -1) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return null;
		}

		// Retrieve playlist with both number and name
		// If there are playlists with the same name, prompt with the number they should play with

		return PlaylistDB.getUserPlaylist(ctx.getEventMember().getUser(), playlistNum - 1);
	}
}
