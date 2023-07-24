package com.raikuman.troubleclub.radio.listener;

import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an event listener for voice channels, detecting if any users leave. The bot will leave
 * after there are no more users.
 *
 * @version 1.5 2023-29-06
 * @since 1.0
 */
public class VoiceEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(VoiceEventListener.class);

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + VoiceEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getEffectiveName());
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
		Member self = event.getGuild().getSelfMember();
		GuildVoiceState selfVoiceState = self.getVoiceState();

		if (selfVoiceState == null)
			return;

		if (!selfVoiceState.inAudioChannel())
			return;

		if (event.getChannelLeft() != selfVoiceState.getChannel())
			return;

		if (event.getChannelLeft() == null)
			return;

		int numPeople = 0;
		for (Member member : event.getChannelLeft().getMembers()) {
			if (!member.getUser().isBot())
				numPeople++;
		}

		if (numPeople > 0)
			return;

		event.getGuild().getAudioManager().closeAudioConnection();

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();
		trackScheduler.queue.clear();
		trackScheduler.audioPlayer.stopTrack();
		trackScheduler.repeat = false;
		trackScheduler.repeatQueue = false;

		musicManager.getAudioPlayer().setPaused(false);
	}
}
