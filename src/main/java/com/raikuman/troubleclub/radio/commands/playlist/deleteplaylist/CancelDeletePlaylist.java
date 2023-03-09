package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 * Button handles canceling the delete playlist prompt
 *
 * @version 1.0 2023-13-01
 * @since 1.2
 */
public class CancelDeletePlaylist implements ButtonInterface {

	@Override
	public void handle(ButtonContext ctx) {
		MessageResources.timedMessage(
			":thumbsup:",
			ctx.getEvent().getChannel().asTextChannel(),
			5
		);

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getButtonId() {
		return "CancelDeletePlaylist";
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
