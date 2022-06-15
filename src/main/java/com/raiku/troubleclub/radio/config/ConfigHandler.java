package com.raiku.troubleclub.radio.config;

import com.raiku.troubleclub.radio.helpers.FileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides methods to load and write configs
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class ConfigHandler {

	private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);
	private static final String DEFAULT_DIRECTORY = "config";

	/**
	 * Loads config settings from file
	 * @param fileName Config file name
	 * @param configName Config name
	 * @return Config setting string
	 */
	public static String loadConfig(String fileName, String configName) {
		File file = new File(DEFAULT_DIRECTORY + "/" + fileName + ".cfg");

		if (!file.exists()) {
			logger.info("Config file " + file.getName() + " does not exist");
			return null;
		}

		String readConfig = null;
		for (String arrayString : FileLoader.readFileToArray(file)) {
			if (arrayString.toLowerCase().contains(configName.toLowerCase())) {
				readConfig = arrayString;
				break;
			}
		}

		if (readConfig != null) {
			return readConfig.split("=")[1].toLowerCase();
		} else {
			return null;
		}
	}

	/**
	 * Writes config settings to file
	 * @param fileName Config file name
	 * @param configName Config name
	 * @param configSetting Config setting
	 */
	public static void writeConfig(String fileName, String configName, String configSetting) {
		File file = new File(DEFAULT_DIRECTORY + "/" + fileName + ".cfg");

		if (!file.exists()) {
			logger.info("Config file " + file.getName() + " does not exist");
			return;
		}

		if (loadConfig(fileName, configName) != null) {
			logger.info("Config setting " + configName + " already exists in config file " + file.getName());
			return;
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(configName + "=" + configSetting);
		} catch (IOException e) {
			logger.info("Could not find file " + file.getName());
		}
	}
}
