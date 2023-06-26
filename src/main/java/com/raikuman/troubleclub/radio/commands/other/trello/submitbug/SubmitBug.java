package com.raikuman.troubleclub.radio.commands.other.trello.submitbug;

import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.components.components.ModalComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.context.SlashContext;
import com.raikuman.botutilities.invokes.interfaces.SlashInterface;
import com.raikuman.botutilities.invokes.manager.InvokeType;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Handles submitting a bug to a specified Trello board
 *
 * @version 1.4 2023-25-06
 * @since 1.2
 */
public class SubmitBug extends ComponentInvoke implements SlashInterface {

	public SubmitBug() {
		componentHandler = ComponentHandler.modal(new ModalComponent(new SubmitBugModal()));
	}

	@Override
	public void handle(SlashContext ctx) {
		componentHandler.provideModalComponent().handleContext(ctx);
	}

	@Override
	public String getInvoke() {
		return "radio-bug";
	}

	@Override
	public String getDescription() {
		return "Report a bug about Trouble Club Radio";
	}

	@Override
	public CommandData getCommandData() {
		return Commands.slash(getInvoke(), getDescription());
	}

	@Override
	public InvokeType getType() {
		return InvokeType.SLASH;
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}