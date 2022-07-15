package com.raikuman.troubleclub.radio.commands.help;

import com.raikuman.botutilities.buttons.pagination.manager.PageInvokeInterface;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.slashcommands.manager.SlashContext;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;

/**
 * Handles sending a pagination of commands on the bot
 *
 * @version 2.2 2022-15-07
 * @since 1.1
 */
public class Help implements SlashInterface, PageInvokeInterface {

	@Override
	public void handle(SlashContext ctx) {
		ctx.getEvent().replyEmbeds(HelpResources.getHomePagination(ctx).buildEmbeds().get(0).build())
			.addActionRows(HelpResources.getHomeActionRows(ctx)).setEphemeral(true).queue();
	}

	@Override
	public String getInvoke() {
		return "help";
	}

	@Override
	public String getDescription() {
		return "A little help for the uninitiated";
	}

	@Override
	public CommandData getCommandData() {
		return Commands.slash(getInvoke(), getDescription());
	}

	@Override
	public String pageName() {
		return "Music Command Categories";
	}

	@Override
	public List<String> pageStrings(EventContext eventContext) {
		return HelpResources.homePageStrings(eventContext.getGuild().getIdLong());
	}

	@Override
	public int itemsPerPage() {
		return 1;
	}

	@Override
	public boolean loopPagination() {
		return true;
	}

	@Override
	public boolean addHomeBtn() {
		return false;
	}

	@Override
	public boolean addFirstPageBtn() {
		return false;
	}
}
