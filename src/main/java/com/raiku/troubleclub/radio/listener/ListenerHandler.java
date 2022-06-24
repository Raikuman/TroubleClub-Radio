package com.raiku.troubleclub.radio.listener;

import com.raiku.troubleclub.radio.commands.*;
import com.raiku.troubleclub.radio.commands.Queue;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PaginationButtonProvider;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 1.3 2020-23-06
 * @since 1.0
 */
public class ListenerHandler {

	/**
	 * Creates a listener manager
	 * @return The listener manager
	 */
	public static ListenerManager getListenerManager() {
		return new ListenerBuilder()
			.setListeners(provideListeners())
			.setCommands(provideCommands())
			.setButtons(provideButtons())
			.build();
	}

	/**
	 * Provides listener adapters to create a listener manager
	 * @return The list of listener adapters
	 */
	private static List<ListenerAdapter> provideListeners() {
		return List.of(new VoiceEventListener());
	}

	/**
	 * Provides commands to create a listener manager
	 * @return The list of commands
	 */
	private static List<CommandInterface> provideCommands() {
		List<CommandInterface> commands = new ArrayList<>(getCommands());
		commands.add(new Help(getCommands()));

		return commands;
	}

	/**
	 * Returns an array of commands
	 * @return The array of commands
	 */
	private static List<CommandInterface> getCommands() {
		return Arrays.asList(
			new Clear(),
			new Join(),
			new Leave(),
			new Loop(),
			new Move(),
			new NowPlaying(),
			new Pause(),
			new Play(),
			new PlayNow(),
			new PlayTop(),
			new Prune(),
			new Queue(),
			new Repeat(),
			new Resume(),
			new Rewind(),
			new Shuffle(),
			new Skip(),
			new SkipTo(),
			new Stop(),
			new Volume()
		);
	}

	/**
	 * Provides buttons to create a listener manager
	 * @return The list of buttons
	 */
	private static List<ButtonInterface> provideButtons() {
		List<ButtonInterface> buttonInterfaces = new ArrayList<>();
		buttonInterfaces.addAll(new PaginationButtonProvider(new Queue()).provideButtons());
		buttonInterfaces.addAll(new PaginationButtonProvider(new Help(getCommands())).provideButtons());

		return buttonInterfaces;
	}
}
