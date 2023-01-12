package com.raikuman.troubleclub.radio.music;

import java.util.List;

/**
 * Provides an object to move around playlist information
 *
 * @version 1.0 2023-11-01
 * @since 1.2
 */
public class PlaylistInfo {

	private final String name;
	private final List<String> songs;
	private final long memberId;

	public PlaylistInfo(String name, List<String> songs, long memberId) {
		this.name = name;
		this.songs = songs;
		this.memberId = memberId;
	}

	public String getName() {
		if (name.isEmpty())
			return "Unnamed Cassette";
		else
			return name;
	}

	public List<String> getSongs() {
		return songs;
	}

	public long getMemberId() { return memberId; }
}
