package com.raikuman.troubleclub.radio.commands.help.selects;

import com.raikuman.botutilities.buttons.pagination.manager.PageInvokeInterface;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.selectmenus.manager.SelectContext;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.commands.help.HelpUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;

/**
 * Handles showing the description of playlist commands
 *
 * @version 1.1 2023-08-03
 * @since 1.2
 */
public class PlaylistSelect implements SelectInterface, PageInvokeInterface {

	@Override
	public void handle(SelectContext ctx) {
		HelpUtilities.handleHelpSelect(
			ctx,
			getMenuValue(),
			pageName(),
			pageStrings(ctx),
			itemsPerPage(),
			loopPagination()
		);
	}

	@Override
	public String getMenuValue() {
		return "helpplaylist";
	}

	@Override
	public String getLabel() {
		return "Playlist";
	}

	@Override
	public String pageName() {
		return "Playlist Commands";
	}

	@Override
	public List<String> pageStrings(EventContext ctx) {
		return HelpUtilities.getHelpManager(ctx).provideHelpCategoryPageStrings(new PlaylistCategory().getName());
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
		return true;
	}

	@Override
	public boolean addFirstPageBtn() {
		return true;
	}

	@Override
	public List<ActionRow> homeActionRows(EventContext ctx) {
		return HelpUtilities.getHelpManager(ctx).provideHomeActionRows();
	}

	@Override
	public List<EmbedBuilder> homePages(EventContext ctx) {
		return HelpUtilities.getHelpManager(ctx).getHomePagination().buildEmbeds();
	}
}
