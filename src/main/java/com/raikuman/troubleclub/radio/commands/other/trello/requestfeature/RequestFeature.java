package com.raikuman.troubleclub.radio.commands.other.trello.requestfeature;

import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.components.components.ModalComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.context.SlashContext;
import com.raikuman.botutilities.invokes.interfaces.SlashInterface;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Handles requesting a feature to a specified Trello board
 *
 * @version 1.4 2023-25-06
 * @since 1.2
 */
public class RequestFeature extends ComponentInvoke implements SlashInterface {

	public RequestFeature() {
		componentHandler = ComponentHandler.modal(new ModalComponent(new RequestFeatureModal()));
	}

	@Override
	public void handle(SlashContext ctx) {
		componentHandler.provideModalComponent().handleContext(ctx);
	}

	@Override
	public String getInvoke() {
		return "radio-feature";
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
}