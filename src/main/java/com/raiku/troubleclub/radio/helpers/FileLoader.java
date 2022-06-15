package com.raiku.troubleclub.radio.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides method to read from files
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class FileLoader {

	private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);

	/**
	 * Reads a file into a list of strings
	 * @param file File to read from
	 * @return List of strings
	 */
	public static List<String> readFileToArray(File file) {
		List<String> fileArray = new ArrayList<>();

		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((line = reader.readLine()) != null)
				fileArray.add(line);

			reader.close();
		} catch (IOException e) {
			logger.info("Could not find file " + file.getName());
		}

		return fileArray;
	}
}
