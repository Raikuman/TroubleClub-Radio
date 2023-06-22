package com.raikuman.troubleclub.radio.category;

import com.raikuman.botutilities.invokes.CategoryInterface;

/**
 * Provides name and emoji for the playlist category
 *
 * @version 1.1 2023-22-06
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
