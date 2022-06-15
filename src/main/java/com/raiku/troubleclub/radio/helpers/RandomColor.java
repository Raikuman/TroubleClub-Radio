package com.raiku.troubleclub.radio.helpers;

import java.awt.*;
import java.util.Random;

/**
 * Provides function to get a randomized Color object
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class RandomColor {

	private final static Random random = new Random();

	/**
	 * Randomly generate a color
	 * @return Random Color object
	 */
	public static Color getRandomColor() {
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();

		return new Color(r, g, b);
	}
}
