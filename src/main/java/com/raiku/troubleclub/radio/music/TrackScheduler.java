package com.raiku.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Schedules tracks to play from a queue
 *
 * @version 1.0 2022-23-06
 * @since 1.0
 */
public class TrackScheduler extends AudioEventAdapter {

	public final AudioPlayer audioPlayer;
	public final BlockingQueue<AudioTrack> queue;
	public boolean repeat = false;
	public boolean repeatQueue = false;

	public TrackScheduler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		this.queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack audioTrack, AudioTrackEndReason endReason) {
		if (!endReason.mayStartNext)
			return;

		if (repeat) {
			this.audioPlayer.startTrack(audioTrack.makeClone(), true);
			return;
		}

		if (repeatQueue)
			this.queue.offer(audioTrack.makeClone());

		nextTrack();
	}

	public void queue(AudioTrack audioTrack) {
		if (!this.audioPlayer.startTrack(audioTrack, true)) {
			this.queue.offer(audioTrack);
		}
	}

	public void addToTop(AudioTrack audioTrack) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		this.queue.offer(audioTrack);

		for (AudioTrack track : queueTracks)
			this.queue.offer(track);
	}

	public void nextTrack() {
		this.audioPlayer.startTrack(this.queue.poll(), false);
	}
}
