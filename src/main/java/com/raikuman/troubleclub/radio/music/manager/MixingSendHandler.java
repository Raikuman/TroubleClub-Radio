package com.raikuman.troubleclub.radio.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class MixingSendHandler implements AudioSendHandler {

    private final StereoPcmAudioMixer mixer = new StereoPcmAudioMixer(960, true);
    private final AudioPlayer[] audioPlayers;
    private byte[] lastData;

    public MixingSendHandler(AudioPlayer[] audioPlayers) {
        this.audioPlayers = audioPlayers;
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

        for (AudioPlayer audioPlayer : audioPlayers) {
            AudioFrame frame = audioPlayer.provide();

            if (frame != null) {
                mixer.addFrame(frame);
            }
        }

        return mixer.get();
    }
}
