package com.raikuman.troubleclub.radio.commands.playlist;

import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.components.components.PaginationComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.components.pagination.PaginationBuilder;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import kotlin.Triple;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles sending a pagination of playlists of the user
 *
 * @version 1.6 2023-24-07
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
		final TextChannel channel = ctx.getChannel().asTextChannel();
		List<Member> mentioned = ctx.getEvent().getMessage().getMentions().getMembers();

		if (mentioned.size() > 0) {
			// Handle mentioned user with selected playlist
			if (ctx.getArgs().size() == 2) {
				// Check if first arg is mention
				boolean incorrectArgs = !ctx.getArgs().get(0).contains("<@");

				// Check if second arg is playlist number
				List<Triple<String, Integer, Integer>> playlists = PlaylistDB.getBasicPlaylistInfo(mentioned.get(0).getUser());
				// Get playlist number
				int playlistNum = -1;
				try {
					playlistNum = Integer.parseInt(ctx.getArgs().get(1));
					if (playlistNum > playlists.size()) {
						playlistNum = -1;
					}
				} catch (NumberFormatException e) {
					for (int i = 0; i < playlists.size(); i++) {
						if (ctx.getArgs().get(1).equalsIgnoreCase(playlists.get(i).getFirst())) {
							playlistNum = i + 1;
						}
					}
				}

				if (incorrectArgs || playlistNum == -1) {
					MessageResources.timedMessage(
						"You must provide a valid argument for this command: `" + getUsage() + "`",
						channel,
						5
					);
					return;
				}

				if (!getUserPlaylist(ctx, playlistNum - 1, mentioned.get(0).getUser(),
					playlists.get(playlistNum - 1))) {
					MessageResources.timedMessage(
						"Could not load " + mentioned.get(0).getEffectiveName() + "'s cassette",
						channel,
						5
					);
				}
				return;
			}

			componentHandler.providePaginationComponent().updateItems(pageStringsPlaylists(mentioned.get(0).getUser()
				, true));
			componentHandler.providePaginationComponent().updateMember(mentioned.get(0));
			componentHandler.providePaginationComponent().updateTitle(mentioned.get(0).getEffectiveName() + "'s " +
				"Cassettes");
		} else {
			// Handle selected playlist
			if (ctx.getArgs().size() == 1) {
				// Check playlist number
				List<Triple<String, Integer, Integer>> playlists = PlaylistDB.getBasicPlaylistInfo(ctx.getEventMember().getUser());
				// Get playlist number
				int playlistNum = -1;
				try {
					playlistNum = Integer.parseInt(ctx.getArgs().get(0));
					if (playlistNum > playlists.size()) {
						playlistNum = -1;
					}
				} catch (NumberFormatException e) {
					for (int i = 0; i < playlists.size(); i++) {
						if (ctx.getArgs().get(0).equalsIgnoreCase(playlists.get(i).getFirst())) {
							playlistNum = i + 1;
						}
					}
				}

				if (playlistNum == -1) {
					MessageResources.timedMessage(
						"You must provide a valid argument for this command: `" + getUsage() + "`",
						channel,
						5
					);
					return;
				}

				if (!getUserPlaylist(ctx, playlistNum - 1, ctx.getEventMember().getUser(),
					playlists.get(playlistNum - 1))) {
					MessageResources.timedMessage(
						"Could not load your cassette",
						channel,
						5
					);
				}
				return;
			}

			componentHandler.providePaginationComponent().updateItems(pageStringsPlaylists(ctx.getEventMember().getUser(), false));
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
		return "(<@user>) (<cassette #>)";
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

	private List<String> pageStringsPlaylists(User user, boolean otherUser) {
		List<String> stringList = new ArrayList<>();
		List<Triple<String, Integer, Integer>> playlists = PlaylistDB.getBasicPlaylistInfo(user);
		if (playlists.isEmpty()) {
			if (otherUser) {
				return List.of(user.getEffectiveName() + " currently has no cassettes!");
			} else {
				return List.of("You currently have no cassettes!");
			}
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

	private boolean getUserPlaylist(CommandContext ctx, int playlistNum, User user,
									Triple<String, Integer, Integer> playlist) {
		PlaylistInfo playlistInfo = PlaylistDB.getUserPlaylist(user, playlistNum);
		if (playlistInfo == null) return false;

		// Construct playlist
		StringBuilder stringBuilder = new StringBuilder("http://www.youtube.com/watch_videos?video_ids=");
		for (int i = 0; i < playlistInfo.getSongs().size(); i++) {
			stringBuilder.append(playlistInfo.getSongs().get(i));

			if (i < playlistInfo.getSongs().size() - 1) {
				stringBuilder.append(",");
			}
		}

		PlayerManager.getInstance().loadPlaylistInfo(ctx, stringBuilder.toString(), playlist, user, this);
		return true;
	}

	public void showUserPlaylist(CommandContext ctx, AudioPlaylist audioPlaylist,
								 Triple<String, Integer, Integer> playlist, User user) {
		List<String> stringList = new ArrayList<>();
		int songNum = 1;
		for (AudioTrack audioTrack : audioPlaylist.getTracks()) {
			stringList.add(String.format(
				"`%d.` [%s](%s) | `%s`",
				songNum,
				audioTrack.getInfo().title,
				audioTrack.getInfo().uri,
				DateAndTime.formatMilliseconds(audioTrack.getInfo().length)
			));

			songNum++;
		}

		componentHandler.providePaginationComponent().updateItems(stringList);
		componentHandler.providePaginationComponent().updateMember(ctx.getEventMember());
		componentHandler.providePaginationComponent().updateTitle(user.getEffectiveName() + "'s " +
			"Cassette: " + playlist.getFirst());
		componentHandler.providePaginationComponent().handleContext(ctx);
	}
}
