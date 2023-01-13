package com.raikuman.troubleclub.radio.commands.other.trello;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.botutilities.modals.manager.ModalContext;
import com.raikuman.botutilities.modals.manager.ModalInterface;
import com.raikuman.botutilities.slashcommands.manager.SlashContext;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.troubleclub.radio.api.trello.TrelloHandler;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.List;

/**
 * Handles requesting a feature to a specified Trello board
 *
 * @version 1.0 2023-13-01
 * @since 1.2
 */
public class RequestFeature implements SlashInterface, ModalInterface {

	@Override
	public void handle(SlashContext ctx) {
		TextInput subject = TextInput.create(getModalId() + "-text-subject", "Feature",
				TextInputStyle.SHORT)
			.setPlaceholder("Feature requested")
			.setMinLength(4)
			.setMaxLength(100) // or setRequiredRange(10, 100)
			.build();

		TextInput body = TextInput.create(getModalId() + "-text-body", "Description",
				TextInputStyle.PARAGRAPH)
			.setPlaceholder("Description of feature (if needed)")
			.setMinLength(0)
			.setMaxLength(1000)
			.setRequired(false)
			.build();

		Modal modal = Modal.create(getModalId(), "Request Feature")
			.addActionRows(ActionRow.of(subject), ActionRow.of(body))
			.build();

		ctx.getEvent().replyModal(modal).queue();
	}

	@Override
	public String getInvoke() {
		return "feature";
	}

	@Override
	public String getDescription() {
		return "Request a feature for Trouble Club Radio";
	}

	@Override
	public CommandData getCommandData() {
		return Commands.slash(getInvoke(), getDescription());
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}

	@Override
	public void handle(ModalContext ctx) {
		ctx.getEvent().reply("Thank you for your request!").setEphemeral(true).queue();

		List<ModalMapping> modalMaps = ctx.getEvent().getValues();

		if (modalMaps.size() != 2)
			return;

		TrelloHandler.createCard(
			EnvLoader.get("trellolistfeatures"),
			EnvLoader.get("trellolabelfeatures"),
			modalMaps.get(0).getAsString(),
			modalMaps.get(1).getAsString()
		);
	}

	@Override
	public String getModalId() {
		return "modal-" + getInvoke();
	}
}
