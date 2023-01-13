package com.raikuman.troubleclub.radio.category;

import com.raikuman.botutilities.commands.manager.CategoryInterface;

/**
 * Provides name and emoji for the playlist category
 *
 * @version 1.0 2022-03-08
 * @since 1.2
 */
public class PlaylistCategory implements CategoryInterface {

	@Override
	public String getName() {
		return "playlist";
	}

	@Override
	public String getEmoji() {
		return "\uD83D\uDCFC";
	}
}
