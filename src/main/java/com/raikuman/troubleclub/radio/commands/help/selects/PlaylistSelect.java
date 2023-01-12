package com.raikuman.troubleclub.radio.commands.help.selects;

import com.raikuman.botutilities.buttons.pagination.manager.PageInvokeInterface;
import com.raikuman.botutilities.buttons.pagination.manager.Pagination;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.selectmenus.manager.SelectContext;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.commands.help.HelpResources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.Arrays;
import java.util.List;

/**
 * Handles showing the description of playlist commands
 *
 * @version 1.0 2022-04-08
 * @since 1.2
 */
public class PlaylistSelect implements SelectInterface, PageInvokeInterface {

	private final List<CommandInterface> commands;

	public PlaylistSelect(List<CommandInterface> commands) {
		this.commands = HelpResources.parseCategory(commands, new PlaylistCategory());
	}

	@Override
	public void handle(SelectContext ctx) {
		Pagination pagination = new Pagination(
			ctx.getEventMember(),
			getMenuValue(),
			pageName(),
			pageStrings(ctx),
			itemsPerPage(),
			loopPagination()
		);

		List<ItemComponent> componentList = Arrays.asList(
			pagination.provideLeft(),
			pagination.provideHome(),
			pagination.provideFirst(),
			pagination.provideRight()
		);

		ctx.getEvent().getHook().editOriginalEmbeds(pagination.buildEmbeds().get(0).build())
			.setActionRow(componentList)
			.queue();

		ctx.getEvent().deferEdit().queue();
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
	public List<String> pageStrings(EventContext eventContext) {
		return HelpResources.buildPages(commands, eventContext.getGuild().getIdLong());
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
		return HelpResources.getHomeActionRows(ctx);
	}

	@Override
	public List<EmbedBuilder> homePages(EventContext ctx) {
		return HelpResources.getHomePagination(ctx).buildEmbeds();
	}
}
