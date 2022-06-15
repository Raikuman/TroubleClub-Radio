package com.raiku.troubleclub.radio.config;

import java.util.HashMap;

/**
 * Provides an interface for default configs
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public interface ConfigInterface {

	/**
	 * Gets name of config file
	 * @return Config name
	 */
	String fileName();

	/**
	 * Gets a map of configs
	 * @return Config map
	 */
	HashMap<String, String> getConfigs();
}
