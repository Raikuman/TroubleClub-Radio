package com.raiku.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

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