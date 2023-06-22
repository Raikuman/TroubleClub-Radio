package com.raikuman.troubleclub.radio.category;

import com.raikuman.botutilities.invokes.CategoryInterface;

/**
 * Provides name and emoji for the music category
 *
 * @version 1.1 2023-22-06
 * @since 1.1
 */
public class MusicCategory implements CategoryInterface {

	@Override
	public String getName() {
		return "music";
	}

	@Override
	public String getEmoji() {
		return "\uD83C\uDFB5";
	}
}
