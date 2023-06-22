package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.context.EventContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.botutilities.pagination.manager.PageComponent;
import com.raikuman.botutilities.pagination.manager.PaginationBuilder;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles sending a pagination of playlists of the user
 *
 * @version 1.2 2023-22-06
 * @since 1.2
 */
public class Playlist extends PageComponent implements CommandInterface {

	public Playlist() {
		pagination = new PaginationBuilder(getInvoke())
			.setTitle("Your Cassettes")
			.setItemsPerPage(10)
			.enableLoop(true)
			.enableFirstPageButton(true)
			.build();
	}

	@Override
	public void handle(CommandContext ctx) {
		pagination.updateEmbeds(pageStrings(ctx));
		pagination.updateMember(ctx.getEventMember());

		ctx.getChannel().sendMessageEmbeds(
			pagination.getEmbeds().get(0).build()
		).addComponents(pagination.provideActionRows()).queue();

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

	private List<String> pageStrings(EventContext ctx) {
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
}
