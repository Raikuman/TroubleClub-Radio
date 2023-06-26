package com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.components.components.ButtonComponent;
import com.raikuman.botutilities.invokes.components.components.rows.ButtonRow;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

/**
 * Handles deleting a playlist from a user's playlist collection
 *
 * @version 1.4 2023-25-06
 * @since 1.2
 */
public class DeletePlaylist extends ComponentInvoke implements CommandInterface {

	public DeletePlaylist() {
		componentHandler = ComponentHandler.buttons(new ButtonComponent(
			new ButtonRow(
				new CancelDeletePlaylist(),
				new ConfirmDeletePlaylist()
			)
		));
	}

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		// Check args
		if (ctx.getArgs().size() != 1) {
			MessageResources.timedMessage(
				"You must provide a cassette to delete",
				channel,
				5
			);
			return;
		}

		// Get playlist number
		int playlistNum;
		try {
			playlistNum = Integer.parseInt(ctx.getArgs().get(0));
		} catch (NumberFormatException e) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		PlaylistInfo playlistInfo = PlaylistDB.getPlaylist(ctx.getEventMember().getIdLong(), playlistNum);
		if (playlistInfo == null) {
			MessageResources.timedMessage(
				"You must select a valid cassette to delete",
				channel,
				5
			);
			return;
		}

		// Button confirmation
		EmbedBuilder confirmation = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("\uD83D\uDCFC Are you sure you want to delete Cassette \"" +
					playlistInfo.getName() + "\"?",
				null,
				ctx.getEventMember().getEffectiveAvatarUrl());

		ctx.getEvent().getChannel().sendMessageEmbeds(confirmation.build())
			.setComponents(componentHandler.asActionRows(ctx)).queue();
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "deleteplaylist";
	}

	@Override
	public String getUsage() {
		return "<playlist #>";
	}

	@Override
	public String getDescription() {
		return "Deletes a playlist";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"dpl",
			"deletecassette",
			"dcass"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new PlaylistCategory();
	}
}
