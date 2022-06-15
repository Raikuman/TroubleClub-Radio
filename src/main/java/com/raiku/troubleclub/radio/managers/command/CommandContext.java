package com.raiku.troubleclub.radio.managers.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * Provides event information for commands
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class CommandContext {

	private final MessageReceivedEvent event;
	private final List<String> args;

	public CommandContext(MessageReceivedEvent event, List<String> args) {
		this.event = event;
		this.args = args;
	}

	/**
	 * Gets the message event from the guild
	 * @return Returns message event
	 */
	public MessageReceivedEvent getEvent() {
		return event;
	}

	/**
	 * Gets list of arguments from the event
	 * @return Returns list of arguments
	 */
	public List<String> getArgs() {
		return args;
	}

	/**
	 * Gets the member who invoked the event
	 * @return Returns event member
	 */
	public Member getEventMember() {
		return event.getMember();
	}

	/**
	 * Gets the guild from the event
	 * @return Returns event guild
	 */
	public Guild getGuild() {
		return event.getGuild();
	}

	/**
	 * Gets the text channel from the event
	 * @return Returns event text channel
	 */
	public TextChannel getChannel() {
		return event.getTextChannel();
	}
}
