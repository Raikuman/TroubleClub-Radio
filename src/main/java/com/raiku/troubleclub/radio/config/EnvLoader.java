package com.raiku.troubleclub.radio.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Loader for .env files
 *
 * @version 1.1 2022-15-06
 * @since 1.0
 */
public class EnvLoader {

	private static final Dotenv dotenv = Dotenv.load();

	/**
	 * Gets value from .env
	 * @param key Key to search for
	 * @return Value of key
	 */
	public static String get(String key) {
		return dotenv.get(key.toUpperCase());
	}
}
