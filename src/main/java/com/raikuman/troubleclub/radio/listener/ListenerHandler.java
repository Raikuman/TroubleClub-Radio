package com.raikuman.troubleclub.radio.listener;

import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 2.1 2022-15-07
 * @since 1.1
 */
public class ListenerHandler {

	/**
	 * Creates a listener manager
	 * @return The listener manager
	 */
	public static ListenerManager getListenerManager() {
		return new ListenerBuilder()
			.setListeners(provideListeners())
			.setCommands(InvokeInterfaceProvider.provideCommands())
			.setButtons(InvokeInterfaceProvider.provideButtons())
			.setSelects(InvokeInterfaceProvider.provideSelects())
			.setSlashes(InvokeInterfaceProvider.provideSlashes())
			.build();
	}

	/**
	 * Provides listener adapters to create a listener manager
	 * @return The list of listener adapters
	 */
	private static List<ListenerAdapter> provideListeners() {
		return List.of(new VoiceEventListener());
	}
}
