package com.raikuman.troubleclub.radio.commands.help;

import com.raikuman.botutilities.buttons.pagination.manager.PageInvokeInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.context.EventContext;

import java.util.List;

/**
 * Handles sending a pagination of commands on the bot
 *
 * @version 2.1 2022-13-07
 * @since 1.1
 */
public class Help implements CommandInterface, PageInvokeInterface {

	@Override
	public void handle(CommandContext ctx) {
		ctx.getChannel().sendMessageEmbeds(HelpResources.getHomePagination(ctx).buildEmbeds().get(0).build())
			.setActionRows(HelpResources.getHomeActionRows(ctx)).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "help";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
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
