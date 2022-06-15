package com.raiku.troubleclub.radio.config;

import com.raiku.troubleclub.radio.config.defaults.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Writes default config files
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class DefaultConfigWriter {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConfigWriter.class);
	private static final String DEFAULT_DIRECTORY = "config";
	private static List<ConfigInterface> configInterfaces;

	public DefaultConfigWriter() {
		configInterfaces = List.of(
			new BotConfig()
		);
	}

	/**
	 * Writes all default configs
	 */
	public static void writeDefaultConfigs() {
		if (new File(DEFAULT_DIRECTORY).mkdirs())
			logger.info("Default directory created");

		BufferedWriter writer;
		File file;
		for (ConfigInterface config : configInterfaces) {
			List<String> directories = new ArrayList<>(List.of(config.fileName().split("/")));
			directories.remove(directories.size() - 1);

			if (directories.size() > 0) {
				StringBuilder configDirectory = new StringBuilder(DEFAULT_DIRECTORY);

				for (String stringDirectory : directories)
					configDirectory.append("/").append(stringDirectory);

				file = new File(configDirectory.toString());

				if (!file.mkdirs())
					logger.info("Config directory already exists. Continuing...");
			}

			file = new File(DEFAULT_DIRECTORY + "/" + config.fileName());

			if (file.exists()) {
				logger.info("Config file " + file.getName() + " already exists. Continuing...");
				continue;
			}

			try {
				if (file.createNewFile()) {
					logger.info("Created config file " + file.getName() + " using defaults");
				} else {
					logger.info("Could not create config file " + file.getName());
					continue;
				}

				writer = new BufferedWriter(new FileWriter(file));

				for (Map.Entry<String, String> configName : config.getConfigs().entrySet())
					writer.write(configName.getKey() + "=" + configName.getValue());

				writer.close();
			} catch (IOException e) {
				logger.info("Could not write to file " + file.getName());
			}
		}
	}
}
