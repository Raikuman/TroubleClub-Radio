package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.context.ButtonContext;
import com.raikuman.botutilities.invokes.interfaces.ButtonInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.time.Duration;

/**
 * Button handles canceling the delete playlist prompt
 *
 * @version 1.4 2023-27-07
 * @since 1.2
 */
public class CancelDeletePlaylist implements ButtonInterface {

	@Override
	public void handle(ButtonContext ctx) {
		String playlist = ctx.getEvent().getMessage().getEmbeds().get(0).getTitle();
		if (playlist == null) {
			return;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setTitle(playlist.substring(12))
			.setAuthor("\uD83D\uDDD1\uFE0F Cassette was not deleted:",
				null,
				ctx.getEventMember().getEffectiveAvatarUrl());

		ctx.getEvent().editMessageEmbeds(builder.build()).queue();
		ctx.getEvent().getMessage().editMessageComponents()
			.delay(Duration.ofSeconds(7))
			.flatMap(Message::delete)
			.queue();
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
