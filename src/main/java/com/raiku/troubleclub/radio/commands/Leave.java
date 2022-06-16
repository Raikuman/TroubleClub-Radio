package com.raiku.troubleclub.radio.commands;

import com.raiku.troubleclub.radio.managers.command.CommandContext;
import com.raiku.troubleclub.radio.managers.command.CommandInterface;

import java.util.List;

/**
 * Handles the bot leaving the voice channel of a user
 *
 * @version 1.1 2022-15-06
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
		return List.of("fuckoff");
	}
}
