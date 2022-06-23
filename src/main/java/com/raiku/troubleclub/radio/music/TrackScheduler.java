package com.raiku.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Schedules tracks to play from a queue
 *
 * @version 1.4 2022-23-06
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

	public AudioTrackInfo moveTrack(int trackNum) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		AudioTrack audioTrack = queueTracks.remove(trackNum - 1);

		this.queue.offer(audioTrack);

		for (AudioTrack track : queueTracks)
			this.queue.offer(track);

		return audioTrack.getInfo();
	}

	public AudioTrackInfo moveTrack(int trackNum, int position) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		AudioTrack audioTrack = queueTracks.remove(trackNum - 1);

		int currentPos = 1;
		for (AudioTrack track : queueTracks) {
			if (currentPos == position)
				this.queue.offer(audioTrack);

			this.queue.offer(track);
			currentPos++;
		}

		return audioTrack.getInfo();
	}

	public void nextTrack() {
		this.audioPlayer.startTrack(this.queue.poll(), false);
	}

	public int pruneTracks() {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		List<String> identifiers = new ArrayList<>();
		for (AudioTrack track : queueTracks)
			identifiers.add(track.getIdentifier());

		identifiers = new ArrayList<>(new LinkedHashSet<>(identifiers));

		List<AudioTrack> prunedTracks = new ArrayList<>();
		for (AudioTrack track : queueTracks) {
			if (identifiers.contains(track.getIdentifier())) {
				prunedTracks.add(track);
				identifiers.remove(track.getIdentifier());
			}
		}

		for (AudioTrack track : prunedTracks)
			this.queue.offer(track);

		return queueTracks.size() - prunedTracks.size();
	}

	public void rewind() {
		AudioTrack playingTrack = this.audioPlayer.getPlayingTrack();
		if (playingTrack == null)
			return;

		this.audioPlayer.startTrack(playingTrack.makeClone(), false);
	}

	public AudioTrackInfo skipTo(int position) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		for (int i = position - 1; i < queueTracks.size(); i++)
			this.queue.offer(queueTracks.get(i));

		nextTrack();

		return queueTracks.get(position - 1).getInfo();
	}
}
