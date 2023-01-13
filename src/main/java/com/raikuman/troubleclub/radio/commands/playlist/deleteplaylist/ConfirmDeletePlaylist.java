package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.commands.other.trello.SubmitBug;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 * Button handles confirming the delete playlist prompt
 *
 * @version 1.0 2023-13-01
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
				"An error has occurred. Fill out a bug report using `/" + new SubmitBug().getInvoke() + "`",
				channel,
				5
			);
			return;
		}

		if (playlistDeletion == 0) {
			MessageResources.timedMessage(
				"An error has occurred. Fill out a bug report using `/" + new SubmitBug().getInvoke() + "`",
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
	public String getButtonId() {
		return "ConfirmDeletePlaylist";
	}

	@Override
	public Emoji getEmoji() {
		return null;
	}

	@Override
	public String getLabel() {
		return null;
	}
}
