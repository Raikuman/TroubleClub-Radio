package com.raikuman.troubleclub.radio.listener.handler;

import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;
import com.raikuman.troubleclub.radio.listener.MemberEventListener;
import com.raikuman.troubleclub.radio.listener.VoiceEventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 2.4 2023-15-01
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
		return List.of(
			new VoiceEventListener(),
			new MemberEventListener()
		);
	}
}
