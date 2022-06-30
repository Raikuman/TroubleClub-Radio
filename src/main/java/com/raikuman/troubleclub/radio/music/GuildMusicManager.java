package com.raikuman.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import java.util.LinkedHashMap;

/**
 * Builds an object containing information for the guild music manager
 *
 * @version 1.3 2022-30-06
 * @since 1.0
 */
public class GuildMusicManager {

	private final AudioPlayer primaryPlayer;
	private final AudioPlayer secondaryPlayer;
	private final AudioPlayer tertiaryPlayer;
	private final TrackScheduler primaryScheduler;
	private final TrackScheduler secondaryScheduler;
	private final TrackScheduler tertiaryScheduler;
	private int currentAudioTrack;
	public final MixingSendHandler mixingHandler;

	public GuildMusicManager(AudioPlayerManager manager) {
		currentAudioTrack = 1;
		manager.getConfiguration().setOutputFormat(new Pcm16AudioDataFormat(2, 48000, 960, true));

		this.primaryPlayer = manager.createPlayer();
		this.primaryScheduler = new TrackScheduler(this.primaryPlayer);
		this.primaryPlayer.addListener(this.primaryScheduler);

		this.secondaryPlayer = manager.createPlayer();
		this.secondaryScheduler = new TrackScheduler(this.secondaryPlayer);
		this.secondaryPlayer.addListener(this.secondaryScheduler);

		this.tertiaryPlayer = manager.createPlayer();
		this.tertiaryScheduler = new TrackScheduler(this.tertiaryPlayer);
		this.tertiaryPlayer.addListener(this.tertiaryScheduler);

		this.mixingHandler = new MixingSendHandler();
		this.mixingHandler.addSound(this.primaryPlayer);
		this.mixingHandler.addSound(this.secondaryPlayer);
		this.mixingHandler.addSound(this.tertiaryPlayer);

		// Testing equalizer
		this.primaryPlayer.setFilterFactory(new EqualizerFactory());
		this.secondaryPlayer.setFilterFactory(new EqualizerFactory());
		this.tertiaryPlayer.setFilterFactory(new EqualizerFactory());
	}

	public int getCurrentAudioTrack() {
		return this.currentAudioTrack;
	}

	public void setCurrentAudioTrack(int currentAudioTrack) {
		if ((currentAudioTrack < 1) || (currentAudioTrack > 3))
			return;

		this.currentAudioTrack = currentAudioTrack;
	}

	public AudioPlayer getAudioPlayer() {
		switch (this.currentAudioTrack) {
			case 2:
				return this.secondaryPlayer;

			case 3:
				return this.tertiaryPlayer;

			default:
				return this.primaryPlayer;
		}
	}

	public TrackScheduler getTrackScheduler() {
		switch (this.currentAudioTrack) {
			case 2:
				return this.secondaryScheduler;

			case 3:
				return this.tertiaryScheduler;

			default:
				return this.primaryScheduler;
		}
	}

	public LinkedHashMap<AudioPlayer, TrackScheduler> getPlayerMap() {
		LinkedHashMap<AudioPlayer, TrackScheduler> playerMap = new LinkedHashMap<>();
		playerMap.put(primaryPlayer, primaryScheduler);
		playerMap.put(secondaryPlayer, secondaryScheduler);
		playerMap.put(tertiaryPlayer, tertiaryScheduler);

		return playerMap;
	}
}
