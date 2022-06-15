package com.raiku.troubleclub.radio.helpers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;

/**
 * Provides methods to easily construct methods commonly used in the bot
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class MessageResources {

	/**
	 * Sends a timed message to a text channel
	 * @param message Message to send
	 * @param channel Channel to send message
	 * @param numSeconds Seconds until deletion
	 */
	public static void timedMessage(String message, TextChannel channel, int numSeconds) {
		channel.sendMessage(message)
			.delay(Duration.ofSeconds(numSeconds))
			.flatMap(Message::delete)
			.queue();
	}

	/**
	 * Sends a voice chat connection error to a text channel
	 * @param channel Channel to send message
	 * @param numSeconds Seconds until deletion
	 */
	public static void connectError(TextChannel channel, int numSeconds) {
		String msg = "Could not connect to your channel. Please contact a server admin about this issue.";
		channel.sendMessage(msg)
			.delay(Duration.ofSeconds(numSeconds))
			.flatMap(Message::delete)
			.queue();
	}
}
