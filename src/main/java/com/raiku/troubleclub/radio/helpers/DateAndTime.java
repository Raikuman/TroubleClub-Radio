package com.raiku.troubleclub.radio.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Provides methods for date, time, and formatting milliseconds
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class DateAndTime {

	/**
	 * Returns local time of bot
	 * @return Current time
	 */
	public static String getTime() {
		DateFormat time = new SimpleDateFormat("hh:mm aa");
		return time.format(new Date());
	}

	/**
	 * Returns local date of bot
	 * @return Current date
	 */
	public static String getDate() {
		DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		return date.format(new Date());
	}

	/**
	 * Formats given long into readable format HH/MM/SS
	 * @param timeInMillis Time in milliseconds
	 * @return Formatted time
	 */
	public static String formatMilliseconds(long timeInMillis) {
		return String.format("%02d:%02d:%02d",
			TimeUnit.MILLISECONDS.toHours(timeInMillis),
			TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
			TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
		);
	}
}
