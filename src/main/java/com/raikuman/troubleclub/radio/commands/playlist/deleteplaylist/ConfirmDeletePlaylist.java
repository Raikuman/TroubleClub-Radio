package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.context.ButtonContext;
import com.raikuman.botutilities.invokes.interfaces.ButtonInterface;
import com.raikuman.troubleclub.radio.commands.other.Changelog;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * Button handles confirming the delete playlist prompt
 *
 * @version 1.3 2023-25-06
 * @since 1.2
 */
public class ConfirmDeletePlaylist implements ButtonInterface {

	@Override
	public void handle(ButtonContext ctx) {
		final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();

		String label = ctx.getEvent().getButton().getLabel();

		int playlistDeletion;
		try {
			playlistDeletion = Integer.parseInt(String.valueOf(label.charAt(9)));
		} catch (NumberFormatException e) {
			MessageResources.timedMessage(
				"An error has occurred. Please report issues to the Github using the `" + new Changelog().getInvoke() + "` command",
				channel,
				5
			);
			return;
		}

		if (playlistDeletion == 0) {
			MessageResources.timedMessage(
				"An error has occurred. Please report issues to the Github using the `" + new Changelog().getInvoke() + "` command",
				channel,
				5
			);
			return;
		}

		// Delete playlist
		if (!PlaylistDB.deletePlaylist(playlistDeletion, ctx.getEventMember().getIdLong())) {
			MessageResources.timedMessage(
				"Could not delete your cassette: `" + playlistDeletion + "`",
				channel,
				5
			);
		}

		int numSubstring = 7 + String.valueOf(playlistDeletion).length();
		String playlistName = label.substring(numSubstring);

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("\uD83D\uDDD1️ Cassette \"" + playlistName + "\" deleted",	null,
				ctx.getEventMember().getEffectiveAvatarUrl());

		channel.sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public Emoji displayEmoji() {
		return null;
	}

	@Override
	public String displayLabel() {
		return null;
	}

	@Override
	public ButtonStyle buttonStyle() {
		return ButtonStyle.SECONDARY;
	}

	@Override
	public String getInvoke() {
		return "ConfirmDeletePlaylist";
	}
}
