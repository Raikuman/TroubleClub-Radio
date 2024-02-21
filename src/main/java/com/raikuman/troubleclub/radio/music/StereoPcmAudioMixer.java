package com.raikuman.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

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
            .order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer();
    }

    public void reset() {
        isEmpty = true;
        onlyFrame = null;
    }

    public void addFrame(AudioFrame frame) {
        if (frame == null) return;

        byte[] data = frame.getData();
        if (isEmpty) {
            isEmpty = false;
            onlyFrame = data;
        } else {
            // Handle onlyFrame
            if (onlyFrame != null) {
                ShortBuffer inputBuffer = ByteBuffer.wrap(onlyFrame)
                    .order(Pcm16AudioDataFormat.CODEC_NAME_BE.equals(frame.getFormat().codecName()) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer();

                for (int i = 0; i < mixBuffer.length; i++) {
                    mixBuffer[i] = inputBuffer.get();
                }

                onlyFrame = null;
            }

            // Handle input frame
            ShortBuffer inputBuffer = ByteBuffer.wrap(data)
                .order(Pcm16AudioDataFormat.CODEC_NAME_BE.equals(frame.getFormat().codecName()) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer();

            // Mix buffer
            for (int i = 0; i < mixBuffer.length; i++) {
                mixBuffer[i] += inputBuffer.get();
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
