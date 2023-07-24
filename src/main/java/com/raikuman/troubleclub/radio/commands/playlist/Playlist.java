package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.components.components.PaginationComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.components.pagination.PaginationBuilder;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.context.EventContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import kotlin.Triple;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles sending a pagination of playlists of the user
 *
 * @version 1.4 2023-06-07
 * @since 1.2
 */
public class Playlist extends ComponentInvoke implements CommandInterface {

	public Playlist() {
		componentHandler = ComponentHandler.pagination(new PaginationComponent(
			new PaginationBuilder(getInvoke())
				.setTitle("Your Cassettes")
				.setItemsPerPage(10)
				.enableLoop(true)
				.enableFirstPageButton(true)
				.build()
		));
	}

	@Override
	public void handle(CommandContext ctx) {
		List<Member> mentioned = ctx.getEvent().getMessage().getMentions().getMembers();

		if (mentioned.size() > 0) {
			componentHandler.providePaginationComponent().updateItems(pageStrings(ctx, mentioned.get(0).getUser()));
			componentHandler.providePaginationComponent().updateMember(mentioned.get(0));
			componentHandler.providePaginationComponent().updateTitle(mentioned.get(0).getEffectiveName() + "'s " +
				"Cassettes");
		} else {
			componentHandler.providePaginationComponent().updateItems(pageStrings(ctx, ctx.getEventMember().getUser()));
			componentHandler.providePaginationComponent().updateMember(ctx.getEventMember());
			componentHandler.providePaginationComponent().updateTitle("Your Cassettes");
		}

		componentHandler.providePaginationComponent().handleContext(ctx);
	}

	@Override
	public String getInvoke() {
		return "playlist";
	}

	@Override
	public String getUsage() {
		return "(<cassette #>)";
	}

	@Override
	public String getDescription() {
		return "Look at your cassettes, or take a look at other peoples' stash!";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"pl",
			"cassette",
			"cass"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new PlaylistCategory();
	}

	private List<String> pageStrings(EventContext ctx, User user) {
		List<String> stringList = new ArrayList<>();
		List<Triple<String, Integer, Integer>> playlists = PlaylistDB.getBasicPlaylistInfo(user);
		if (playlists.isEmpty()) {
			return List.of("You currently have no cassettes!");
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < playlists.size(); i++) {
			builder
				.append("`")
				.append(i + 1)
				.append(".` ")
				.append(playlists.get(i).getFirst())
				.append(" `")
				.append(playlists.get(i).getSecond())
				.append(" song");

			// Handle plurality
			if (playlists.get(i).getSecond() > 1) {
				builder.append("s");
			}

			builder.append("`");

			stringList.add(builder.toString());
			builder = new StringBuilder();
		}

		return stringList;
	}
}
