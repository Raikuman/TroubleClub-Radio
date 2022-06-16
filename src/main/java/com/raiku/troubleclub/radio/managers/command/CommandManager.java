package com.raiku.troubleclub.radio.managers.command;

import com.raiku.troubleclub.radio.commands.*;
import com.raiku.troubleclub.radio.config.ConfigHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides a command manager object to handle commands
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class CommandManager {

	private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
	private final List<CommandInterface> commands = new ArrayList<>();

	public CommandManager() {
		addCommand(new Join());
		addCommand(new Play());
		addCommand(new Leave());
		addCommand(new Stop());
		addCommand(new Pause());
		addCommand(new Resume());
		addCommand(new Queue());
		addCommand(new Repeat());
		addCommand(new Loop());
	}

	/**
	 * Gets list of commands
	 * @return Returns list of commands
	 */
	public List<CommandInterface> getCommands() {
		return commands;
	}

	/**
	 * Adds a command to the manager, checking for duplicates
	 * @param command Command to add
	 */
	private void addCommand(CommandInterface command) {
		boolean commandFound = commands.stream().anyMatch(
			found -> found.getInvoke().equalsIgnoreCase(command.getInvoke())
		);

		if (commandFound) {
			logger.info("A command with this handle already exists: " + command.getInvoke());
			return;
		}

		commands.add(command);
	}

	/**
	 * Gets a command from the manager
	 * @param search String to search with
	 * @return Returns found command
	 */
	public CommandInterface getCommand(String search) {
		for (CommandInterface command : commands)
			if (command.getInvoke().equalsIgnoreCase(search) || command.getAliases().contains(search))
				return command;

		return null;
	}

	/**
	 * Handles invocations of commands and runs them
	 * @param event Provide event for command information
	 */
	public void handle(MessageReceivedEvent event) {
		String prefix = ConfigHandler.loadConfig("settings", "prefix");
		if (prefix == null) {
			logger.info("Could not retrieve prefix");
			return;
		}

		String[] split = event.getMessage().getContentRaw()
			.replaceFirst(
				"(?i)" + Pattern.quote(prefix),
				""
			).split("\\s+");

		CommandInterface command = getCommand(split[0].toLowerCase());

		if (command == null) {
			logger.info("Could not retrieve command from command manager: " + split[0].toLowerCase());
			return;
		}

		List<String> args = Arrays.asList(split).subList(1, split.length);

		command.handle(new CommandContext(event, args));
	}
}
