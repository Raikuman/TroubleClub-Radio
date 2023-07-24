package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.invokes.context.ButtonContext;
import com.raikuman.botutilities.invokes.interfaces.ButtonInterface;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * Button handles canceling the delete playlist prompt
 *
 * @version 1.3 2023-30-06
 * @since 1.2
 */
public class CancelDeletePlaylist implements ButtonInterface {

	@Override
	public void handle(ButtonContext ctx) {
		String playlist = ctx.getEvent().getMessage().getEmbeds().get(0).getTitle();
		if (playlist == null) {
			return;
		}

		MessageResources.timedMessage(
			"**" + playlist.substring(12) + "** was not deleted :thumbsup:",
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
		return "\uD83D\uDDD9";
	}

	@Override
	public ButtonStyle buttonStyle() {
		return ButtonStyle.DANGER;
	}

	@Override
	public String getInvoke() {
		return "CancelDeletePlaylist";
	}
}
