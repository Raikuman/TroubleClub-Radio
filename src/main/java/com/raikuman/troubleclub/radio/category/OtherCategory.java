package com.raikuman.troubleclub.radio.category;

import com.raikuman.botutilities.commands.manager.CategoryInterface;

/**
 * Provides name and emoji for the other category
 *
 * @version 1.0 2022-09-07
 * @since 1.1
 */
public class OtherCategory implements CategoryInterface {

	@Override
	public String getName() {
		return "other";
	}

	@Override
	public String getEmoji() {
		return "\uD83E\uDD5A";
	}
}
