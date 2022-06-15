package com.raiku.troubleclub.radio.music;

import com.raiku.troubleclub.radio.helpers.DateAndTime;
import com.raiku.troubleclub.radio.helpers.MessageResources;
import com.raiku.troubleclub.radio.helpers.RandomColor;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

	private static PlayerManager PLAYER_INSTANCE;
	private final Map<Long, GuildMusicManager> musicManagerMap;
	private final AudioPlayerManager audioPlayerManager;

	public PlayerManager() {
		this.musicManagerMap = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();

		// Check source of track
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagerMap.computeIfAbsent(guild.getIdLong(), guildId -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.sendHandler);
			return guildMusicManager;
		});
	}

	public void loadAndPlay(TextChannel channel, String trackUrl, User user) {
		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

		// Set bot to 50% volume
		musicManager.audioPlayer.setVolume(50);

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				musicManager.trackScheduler.queue(audioTrack);

				channel.sendMessageEmbeds(
					trackEmbed(
						musicManager.trackScheduler.queue.size(),
						audioTrack,
						user
					).build()
				).queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				List<AudioTrack> tracks = audioPlaylist.getTracks();

				// Handle searching songs
				if (trackUrl.contains("ytsearch:")) {
					AudioTrack firstTrack = audioPlaylist.getTracks().remove(0);
					if (firstTrack == null)
						return;

					musicManager.trackScheduler.queue(firstTrack);

					channel.sendMessageEmbeds(
						trackEmbed(
							musicManager.trackScheduler.queue.size(),
							firstTrack,
							user
						).build()
					).queue();

					return;
				}

				// Queue playlist if provided a link
				for (AudioTrack track : tracks)
					musicManager.trackScheduler.queue(track);

				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor("Adding playlist to queue:", null, user.getAvatarUrl())
					.setTitle(audioPlaylist.getName(), tracks.get(0).getInfo().uri)
					.setColor(RandomColor.getRandomColor());
				builder
					.addField("Songs in Playlist", "`" + audioPlaylist.getTracks().size() + "`songs", true);

				channel.sendMessageEmbeds(builder.build()).queue();
			}

			@Override
			public void noMatches() {
				MessageResources.timedMessage(
					"Nothing found using `" + trackUrl + "`",
					channel,
					5
				);
			}

			@Override
			public void loadFailed(FriendlyException e) {
				MessageResources.timedMessage(
					"Could not load track! `" + e.getMessage() + "`",
					channel,
					5
				);
			}
		});
	}

	public static PlayerManager getInstance() {
		if (PLAYER_INSTANCE == null) {
			PLAYER_INSTANCE = new PlayerManager();
		}

		return PLAYER_INSTANCE;
	}

	private EmbedBuilder trackEmbed(int queueSize, AudioTrack audioTrack, User user) {
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor());
		builder
			.addField("Channel", audioTrack.getInfo().author, true)
			.addField("Song Duration", DateAndTime.formatMilliseconds(audioTrack.getDuration()), true);

		String title, nowPlaying;
		if (queueSize == 0) {
			title = "▶️ Playing:";
			nowPlaying = "Now playing";
		} else {
			title = "⏭️ Adding to queue:";
			nowPlaying = String.valueOf(queueSize);
		}

		builder
			.setAuthor(title, audioTrack.getInfo().uri, user.getAvatarUrl())
			.addField("Position in queue", nowPlaying, true);

		return builder;
	}
}
