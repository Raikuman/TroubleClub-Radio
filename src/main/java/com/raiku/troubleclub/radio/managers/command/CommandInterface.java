package com.raiku.troubleclub.radio.managers.command;

import java.util.List;

/**
 * Provides an interface for commands
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public interface CommandInterface {

	/**
	 * Method containing code for running the command
	 * @param ctx Context for command use
	 */
	void handle(CommandContext ctx);

	/**
	 * Gets invocation string
	 * @return Returns invoke string
	 */
	String getInvoke();

	/**
	 * Gets usage string
	 * @return Returns usage string
	 */
	String getUsage();

	/**
	 * Gets description string
	 * @return Returns description string
	 */
	String getDescription();

	/**
	 * Gets different invocations of the same command
	 * @return Return list of aliases
	 */
	default List<String> getAliases() {
		return List.of();
	}
}
