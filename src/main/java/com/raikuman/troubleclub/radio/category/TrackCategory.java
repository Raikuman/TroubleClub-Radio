package com.raikuman.troubleclub.radio.category;

import com.raikuman.botutilities.invokes.CategoryInterface;

/**
 * Provides name and emoji for the track category
 *
 * @version 1.1 2023-22-06
 * @since 1.1
 */
public class TrackCategory implements CategoryInterface {

	@Override
	public String getName() {
		return "track";
	}

	@Override
	public String getEmoji() {
		return "\uD83C\uDFBC";
	}
}
