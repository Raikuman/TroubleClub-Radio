package com.raikuman.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides information on the audio send handler to provide audio from the mixer
 *
 * @version 1.0 2022-29-06
 * @since 1.0
 */
public class MixingSendHandler implements AudioSendHandler {
	private final StereoPcmAudioMixer mixer = new StereoPcmAudioMixer(960, true);
	private final List<AudioPlayer> sounds = new ArrayList<>();

	private byte[] lastData;

	public void addSound(AudioPlayer sound) {
		sounds.add(sound);
	}

	@Override
	public boolean canProvide() {
		checkFrameData();
		return lastData != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		checkFrameData();

		byte[] data = lastData;
		lastData = null;

		return ByteBuffer.wrap(data);
	}

	@Override
	public boolean isOpus() {
		return false;
	}

	private void checkFrameData() {
		if (lastData == null) {
			lastData = getData();
		}
	}

	private byte[] getData() {

		mixer.reset();

		for (AudioPlayer sound : sounds) {
			AudioFrame frame = sound.provide();

			if (frame != null) {
				mixer.add(frame);
			}
		}

		return mixer.get();
	}
}