package com.raiku.troubleclub.radio.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {

	private static final Dotenv dotenv = Dotenv.load();

	public static String get(String key) {
		return dotenv.get(key.toUpperCase());
	}
}