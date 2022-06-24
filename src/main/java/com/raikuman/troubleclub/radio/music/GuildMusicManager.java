package com.raikuman.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Builds an object containing information for the guild music manager
 *
 * @version 1.0 2022-23-06
 * @since 1.0
 */
public class GuildMusicManager {

	public final AudioPlayer audioPlayer;
	public final TrackScheduler trackScheduler;
	public final AudioPlayerSendHandler sendHandler;

	public GuildMusicManager(AudioPlayerManager manager) {
		this.audioPlayer = manager.createPlayer();
		this.trackScheduler = new TrackScheduler(this.audioPlayer);
		this.audioPlayer.addListener(this.trackScheduler);
		this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
	}
}
