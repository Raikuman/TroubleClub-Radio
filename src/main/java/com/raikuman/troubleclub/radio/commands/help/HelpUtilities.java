package com.raikuman.troubleclub.radio.commands.help;

import com.raikuman.botutilities.buttons.pagination.manager.Pagination;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.help.HelpBuilder;
import com.raikuman.botutilities.help.HelpManager;
import com.raikuman.botutilities.selectmenus.manager.SelectContext;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.commands.help.selects.MusicSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.OtherSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.PlaylistSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.TrackSelect;
import com.raikuman.troubleclub.radio.listener.handler.InvokeInterfaceProvider;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.Arrays;
import java.util.List;

/**
 * Provides utility functions for the help command
 *
 * @version 1.0 2023-08-03
 * @since 1.2
 */
public class HelpUtilities {

	/**
	 * Creates a help manager for the help command
	 * @param ctx The event context to use in the help manager
	 * @return The built help manager
	 */
	public static HelpManager getHelpManager(EventContext ctx) {
		HelpBuilder builder = new HelpBuilder(
			new Help().pageName(),
			ctx,
			Arrays.asList(
				new MusicCategory(),
				new PlaylistCategory(),
				new TrackCategory(),
				new OtherCategory()
			),
			Arrays.asList(
				new MusicSelect(),
				new PlaylistSelect(),
				new TrackSelect(),
				new OtherSelect()
			)
		);

		builder.setCommands(InvokeInterfaceProvider.provideCommands());
		builder.setSlashes(InvokeInterfaceProvider.provideSlashes());

		return builder.build();
	}

	/**
	 * Handles the functionality of help selects
	 * @param ctx The select context to build pagination from
	 * @param menuValue The menu value of the select
	 * @param pagename The name of the select page
	 * @param pageStrings The strings to create pagination for
	 * @param itemsPerPage The number of items on each page
	 * @param loopPagination Whether to loop the pagination
	 */
	public static void handleHelpSelect(SelectContext ctx, String menuValue, String pagename,
		List<String> pageStrings, int itemsPerPage, boolean loopPagination) {
		Pagination pagination = new Pagination(
			ctx.getEventMember(),
			menuValue,
			pagename,
			pageStrings,
			itemsPerPage,
			loopPagination
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
}
