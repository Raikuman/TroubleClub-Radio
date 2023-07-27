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
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles deleting a playlist from a user's playlist collection
 *
 * @version 1.6 2023-25-07
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

		// Button confirmation
		EmbedBuilder confirmation = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("\uD83D\uDCFC Are you sure you want to delete this Cassette?",
				null,
				ctx.getEventMember().getEffectiveAvatarUrl())
			.setTitle("Cassette " + playlistNum + ": " + playlists.get(playlistNum - 1).getFirst());

		StringBuilder descriptionBuilder = confirmation.getDescriptionBuilder();
		descriptionBuilder
			.append("`")
			.append(playlists.get(playlistNum - 1).getSecond())
			.append(" song");

		if (playlists.get(playlistNum - 1).getSecond() > 1) {
			descriptionBuilder.append("s");
		}

		descriptionBuilder.append("`");

		List<ItemComponent> components = componentHandler.asActionRows(ctx).get(0).getComponents();
		List<Button> buttons = new ArrayList<>();
		for (ItemComponent component : components) {
			if (Button.class.isAssignableFrom(component.getClass())) {
				buttons.add((Button) component);
			}
		}

		buttons.set(1,
			buttons.get(1).withLabel("Delete " + playlists.get(playlistNum - 1).getFirst()).withStyle(ButtonStyle.SUCCESS));

		ctx.getEvent().getChannel().sendMessageEmbeds(confirmation.build())
			.setComponents(ActionRow.of(buttons))
			.delay(Duration.ofSeconds(15))
			.flatMap((message) -> message.editMessageEmbeds(updateEmbed(confirmation, ctx.getEventMember().getEffectiveAvatarUrl()).build()))
			.flatMap(Message::editMessageComponents)
			.delay(Duration.ofSeconds(7))
			.flatMap(Message::delete)
			.queue(null, new ErrorHandler()
				.ignore(ErrorResponse.UNKNOWN_MESSAGE));

		ctx.getEvent().getMessage().delete().queue();
	}

	private EmbedBuilder updateEmbed(EmbedBuilder embedBuilder, String avatarUrl) {
		embedBuilder
			.setAuthor("\uD83D\uDCFC Your Cassette will not be deleted!",
				null,
				avatarUrl);

		return embedBuilder;
	}

	@Override
	public String getInvoke() {
		return "deleteplaylist";
	}

	@Override
	public String getUsage() {
		return "<playlist # or name>";
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
