package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.context.ButtonContext;
import com.raikuman.botutilities.invokes.interfaces.ButtonInterface;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.time.Duration;
import java.util.List;

/**
 * Button handles confirming the delete playlist prompt
 *
 * @version 1.5 2023-27-07
 * @since 1.2
 */
public class ConfirmDeletePlaylist implements ButtonInterface {

	@Override
	public void handle(ButtonContext ctx) {
		final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();

		String playlist = ctx.getEvent().getMessage().getEmbeds().get(0).getTitle();
		if (playlist == null) {
			MessageResources.timedMessage(
				"There was an error deleting your cassette",
				channel,
				5
			);
			return;
		}

		int playlistNum = Integer.parseInt(String.valueOf(playlist.charAt(9)));

		// Get playlist to delete
		List<Triple<String, Integer, Integer>> playlists = PlaylistDB.getBasicPlaylistInfo(ctx.getEventMember().getUser());
		boolean deleted = PlaylistDB.deletePlaylist(playlists.get(playlistNum - 1).getThird());

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setTitle(playlists.get(playlistNum - 1).getFirst());

		if (deleted) {
			builder
				.setAuthor("\uD83D\uDDD1\uFE0F Cassette deleted:",
					null,
					ctx.getEventMember().getEffectiveAvatarUrl());
		} else {
			builder
				.setAuthor("❗ There was an error deleting your cassette:",
				null,
				ctx.getEventMember().getEffectiveAvatarUrl());
		}

		ctx.getEvent().editMessageEmbeds(builder.build()).queue();
		ctx.getEvent().getMessage().editMessageComponents()
			.delay(Duration.ofSeconds(7))
			.flatMap(Message::delete)
			.queue();
	}

	@Override
	public Emoji displayEmoji() {
		return null;
	}

	@Override
	public String displayLabel() {
		return null;
	}

	@Override
	public ButtonStyle buttonStyle() {
		return ButtonStyle.SECONDARY;
	}

	@Override
	public String getInvoke() {
		return "ConfirmDeletePlaylist";
	}
}
