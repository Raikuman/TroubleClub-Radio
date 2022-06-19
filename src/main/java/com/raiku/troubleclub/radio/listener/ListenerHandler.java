package com.raiku.troubleclub.radio.listener;

import com.raiku.troubleclub.radio.commands.*;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PaginationButtonProvider;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;

import java.util.Arrays;
import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 1.0 2022-19-06
 * @since 1.0
 */
public class ListenerHandler {

	/**
	 * Creates a listener manager
	 * @return The listener manager
	 */
	public static ListenerManager getListenerManager() {
		return new ListenerBuilder()
			.setCommands(getCommands())
			.setButtons(getButtons())
			.build();
	}

	/**
	 * Provides commands to create a listener manager
	 * @return The list of commands
	 */
	private static List<CommandInterface> getCommands() {
		return Arrays.asList(
			new Join(),
			new Leave(),
			new Loop(),
			new Pause(),
			new Play(),
			new Repeat(),
			new Resume(),
			new Stop(),
			new Queue(),
			new Abc()
		);
	}

	/**
	 * Provides buttons to create a listener manager
	 * @return The list of buttons
	 */
	private static List<ButtonInterface> getButtons() {
		return new PaginationButtonProvider(new Abc()).provideButtons();
	}
}
