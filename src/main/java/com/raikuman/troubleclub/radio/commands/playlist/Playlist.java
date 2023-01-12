package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.buttons.pagination.manager.PageInvokeInterface;
import com.raikuman.botutilities.buttons.pagination.manager.Pagination;
import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles sending a pagination of playlists of the user
 *
 * @version 1.0 2023-11-01
 * @since 1.2
 */
public class Playlist implements CommandInterface, PageInvokeInterface {

	@Override
	public void handle(CommandContext ctx) {
		Pagination pagination = new Pagination(
			ctx.getEventMember(),
			getInvoke(),
			pageName(),
			pageStrings(ctx),
			itemsPerPage(),
			loopPagination()
		);

		List<ItemComponent> componentList = Arrays.asList(
			pagination.provideLeft(),
			pagination.provideFirst(),
			pagination.provideRight()
		);

		ctx.getChannel().sendMessageEmbeds(
			pagination.buildEmbeds().get(0).build()
		).setActionRow(componentList).queue();

		ctx.getEvent().getMessage().delete().queue();
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
		return "";
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

	@Override
	public String pageName() {
		return "Your Cassettes";
	}

	@Override
	public List<String> pageStrings(EventContext ctx) {
		List<PlaylistInfo> playlistInfoList = PlaylistDB.getMemberPlaylistInfo(ctx.getEventMember().getIdLong());

		if (playlistInfoList.isEmpty())
			return new ArrayList<>(
				List.of("You do not have any cassettes! Use the `" +
					new CreatePlaylist().getInvoke() +
					"` command to create a cassette.")
			);

		List<String> stringList = new ArrayList<>();
		int playlistNum = 1;
		for (PlaylistInfo playlistInfo : playlistInfoList) {
			stringList.add(
				"`" + playlistNum + "` | **" +
				playlistInfo.getName() + "** | *" +
				playlistInfo.getNumSongs() + " songs*"
			);

			playlistNum++;
		}

		return stringList;
	}

	@Override
	public int itemsPerPage() {
		return 10;
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
		return true;
	}
}
