package com.raikuman.troubleclub.radio.commands;

import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;

import java.util.List;

/**
 * Handles the bot leaving the voice channel of a user
 *
 * @version 1.2 2022-18-06
 * @since 1.0
 */
public class Leave implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		boolean stopped = new Stop().stopMusic(ctx);

		if (stopped) {
			ctx.getGuild().getAudioManager().closeAudioConnection();
			ctx.getEvent().getMessage().addReaction("U+1F44B").queue();
		} else {
			ctx.getEvent().getMessage().addReaction("U+1F6AB").queue();
		}
	}

	@Override
	public String getInvoke() {
		return "leave";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Leaves the user's voice channel";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"fuckoff",
			"exit"
		);
	}
}
