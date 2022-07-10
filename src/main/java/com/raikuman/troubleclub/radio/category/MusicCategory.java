package com.raikuman.troubleclub.radio.category;

import com.raikuman.botutilities.commands.manager.CategoryInterface;

/**
 * Provides name and emoji for the music category
 *
 * @version 1.0 2022-09-07
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
