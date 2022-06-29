package com.raikuman.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat.CODEC_NAME_BE;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * Handles mixing audio frames together from AudioPlayers
 *
 * @version 1.0 2022-23-06
 * @since 1.0
 */
public class StereoPcmAudioMixer {
	private final int[] mixBuffer;
	private final byte[] outputBuffer;
	private final ShortBuffer wrappedOutput;
	private final Multiplier previousMultiplier = new Multiplier();
	private final Multiplier currentMultiplier = new Multiplier();
	private byte[] onlyFrame = null;
	private boolean isEmpty = true;

	public StereoPcmAudioMixer(int sampleCount, boolean isBigEndian) {
		this.mixBuffer = new int[sampleCount * 2];
		this.outputBuffer = new byte[sampleCount * 4];
		this.wrappedOutput = ByteBuffer.wrap(outputBuffer)
			.order(isBigEndian ? BIG_ENDIAN : LITTLE_ENDIAN)
			.asShortBuffer();
	}

	public void reset() {
		isEmpty = true;
		onlyFrame = null;
	}

	public void add(AudioFrame frame) {
		if (frame != null) {
			byte[] data = frame.getData();

			if (isEmpty) {
				isEmpty = false;
				onlyFrame = data;
			} else {
				if (onlyFrame != null) {
					ShortBuffer inputBuffer = ByteBuffer.wrap(onlyFrame)
						.order(CODEC_NAME_BE.equals(frame.getFormat().codecName()) ? BIG_ENDIAN : LITTLE_ENDIAN)
						.asShortBuffer();

					for (int i = 0; i < mixBuffer.length; i++) {
						mixBuffer[i] = inputBuffer.get(i);
					}

					onlyFrame = null;
				}

				ShortBuffer inputBuffer = ByteBuffer.wrap(data)
					.order(CODEC_NAME_BE.equals(frame.getFormat().codecName()) ? BIG_ENDIAN : LITTLE_ENDIAN)
					.asShortBuffer();

				for (int i = 0; i < mixBuffer.length; i++) {
					mixBuffer[i] += inputBuffer.get(i);
				}
			}
		}
	}

	public byte[] get() {
		if (isEmpty) {
			previousMultiplier.reset();
			return null;
		} else if (onlyFrame != null) {
			previousMultiplier.reset();
			return onlyFrame;
		}

		updateMultiplier();

		if (!currentMultiplier.identity || !previousMultiplier.identity) {
			for (int i = 0; i < 10; i++) {
				float gradientMultiplier = (currentMultiplier.value * i + previousMultiplier.value * (10 - i)) * 0.1f;
				wrappedOutput.put(i, (short) (gradientMultiplier * mixBuffer[i]));
			}

			for (int i = 10; i < mixBuffer.length; i++) {
				wrappedOutput.put(i, (short) (currentMultiplier.value * mixBuffer[i]));
			}

			previousMultiplier.identity = currentMultiplier.identity;
			previousMultiplier.value = currentMultiplier.value;
		} else {
			for (int i = 0; i < mixBuffer.length; i++) {
				wrappedOutput.put(i, (short) mixBuffer[i]);
			}
		}

		return outputBuffer;
	}

	private void updateMultiplier() {
		int peak = 0;

		if (!isEmpty) {
			for (int value : mixBuffer) {
				peak = Math.max(peak, Math.abs(value));
			}
		}

		if (peak > 32767) {
			currentMultiplier.identity = false;
			currentMultiplier.value = 32767.0f / peak;
		} else {
			currentMultiplier.identity = true;
			currentMultiplier.value = 1.0f;
		}
	}

	private static class Multiplier {
		private boolean identity = true;
		private float value = 1.0f;

		private void reset() {
			identity = true;
			value = 1.0f;
		}
	}
}
