package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.invokes.context.ButtonContext;
import com.raikuman.botutilities.invokes.interfaces.ButtonInterface;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * Button handles canceling the delete playlist prompt
 *
 * @version 1.2 2023-25-06
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
		return "CancelDeletePlaylist";
	}
}
