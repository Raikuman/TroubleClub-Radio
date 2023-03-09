package com.raikuman.troubleclub.radio.commands.help;

import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.help.DefaultHelp;
import com.raikuman.botutilities.help.HelpManager;
import com.raikuman.botutilities.slashcommands.manager.SlashContext;

import java.util.List;

/**
 * Handles the help slash command
 *
 * @version 1.0 2023-08-03
 * @since 1.2
 */
public class Help extends DefaultHelp {

	@Override
	public void handle(SlashContext ctx) {
		HelpManager manager = HelpUtilities.getHelpManager(ctx);

		ctx.getEvent().replyEmbeds(manager.getHomePagination().buildEmbeds().get(0).build())
			.addComponents(manager.provideHomeActionRows()).setEphemeral(true).queue();
	}

	@Override
	public String pageName() {
		return "Music Command Categories";
	}

	@Override
	public String getDescription() {
		return "Shows commands for music";
	}

	@Override
	public List<String> pageStrings(EventContext ctx) {
		return HelpUtilities.getHelpManager(ctx).provideHomePageStrings(ctx);
	}
}
