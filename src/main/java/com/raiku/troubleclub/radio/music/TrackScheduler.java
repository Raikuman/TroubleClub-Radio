package com.raiku.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

	public final AudioPlayer audioPlayer;
	public final BlockingQueue<AudioTrack> queue;

	public TrackScheduler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		this.queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack audioTrack, AudioTrackEndReason endReason) {
		if (!endReason.mayStartNext) {
			return;
		}

		nextTrack();
	}

	public void queue(AudioTrack audioTrack) {
		if (!this.audioPlayer.startTrack(audioTrack, true)) {
			this.queue.offer(audioTrack);
		}
	}

	public void nextTrack() {
		this.audioPlayer.startTrack(this.queue.poll(), false);
	}
}
