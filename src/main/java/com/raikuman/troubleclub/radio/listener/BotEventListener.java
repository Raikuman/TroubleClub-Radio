//package com.raikuman.troubleclub.radio.listener;
//
//import com.raikuman.botutilities.configs.Prefix;
//import com.raikuman.botutilities.invokes.commands.manager.CommandInterface;
//import com.raikuman.botutilities.invokes.commands.manager.CommandManager;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.api.events.session.ReadyEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.jetbrains.annotations.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
///**
// * Provides an event listener for listening to bot commands
// *
// * @version 1.0 2023-19-01
// * @since 1.2
// */
//public class BotEventListener extends ListenerAdapter {
//
//	private static final Logger logger = LoggerFactory.getLogger(BotEventListener.class);
//
//	private final CommandManager manager;
//
//	public BotEventListener(List<CommandInterface> commandInterfaces) {
//		this.manager = new CommandManager(commandInterfaces);
//	}
//
//	@Override
//	public void onReady(@NotNull ReadyEvent event) {
//		logger.info("{}" + BotEventListener.class.getName() + " is initialized",
//			event.getJDA().getSelfUser().getAsTag());
//	}
//
//	@Override
//	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//		if (!event.isFromGuild())
//			return;
//
//		User user = event.getAuthor();
//
//		if (!user.isBot() || event.isWebhookMessage())
//			return;
//
//		String prefix = Prefix.getPrefix(event.getGuild().getIdLong());
//		if (prefix == null) {
//			logger.error("Could not retrieve prefix");
//			return;
//		}
//
//		String raw = event.getMessage().getContentRaw();
//
//		if (raw.startsWith(prefix))
//			manager.handleEvent(event);
//	}
//}