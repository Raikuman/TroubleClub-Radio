package com.raiku.troubleclub.radio.listeners;

import com.raiku.troubleclub.radio.config.ConfigHandler;
import com.raiku.troubleclub.radio.managers.command.CommandManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for commands
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class CommandEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CommandEventListener.class);
	private final CommandManager manager = new CommandManager();

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{} " + CommandEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!event.isFromGuild())
			return;

		User user = event.getAuthor();

		if (user.isBot() || event.isWebhookMessage())
			return;

		String prefix = ConfigHandler.loadConfig("settings", "prefix");
		if (prefix == null) {
			logger.info("Could not retrieve prefix");
			return;
		}

		String raw = event.getMessage().getContentRaw();

		if (raw.startsWith(prefix))
			manager.handle(event);
	}
}
