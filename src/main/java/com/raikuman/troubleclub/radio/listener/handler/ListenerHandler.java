package com.raikuman.troubleclub.radio.listener.handler;

import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.troubleclub.radio.commands.help.Help;
import com.raikuman.troubleclub.radio.listener.BotEventListener;
import com.raikuman.troubleclub.radio.listener.MemberEventListener;
import com.raikuman.troubleclub.radio.listener.VoiceEventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 2.6 2023-08-03
 * @since 1.1
 */
public class ListenerHandler {

	/**
	 * Creates a listener manager
	 * @return The listener manager
	 */
	public static ListenerManager getListenerManager() {
		List<SlashInterface> slashes = InvokeInterfaceProvider.provideSlashes();
		slashes.add(new Help());

		return new ListenerBuilder()
			.setListeners(provideListeners())
			.setCommands(InvokeInterfaceProvider.provideCommands())
			.setButtons(InvokeInterfaceProvider.provideButtons())
			.setSelects(InvokeInterfaceProvider.provideSelects())
			.setSlashes(slashes)
			.setModals(InvokeInterfaceProvider.provideModals())
			.build();
	}

	/**
	 * Provides listener adapters to create a listener manager
	 * @return The list of listener adapters
	 */
	private static List<ListenerAdapter> provideListeners() {
		return List.of(
			new VoiceEventListener(),
			new MemberEventListener(),
			new BotEventListener(InvokeInterfaceProvider.provideBotCommands())
		);
	}
}