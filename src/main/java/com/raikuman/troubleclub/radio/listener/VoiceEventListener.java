package com.raikuman.troubleclub.radio.listener;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Provides an event listener for voice channels, detecting if any users leave. The bot will leave
 * after there are no more users.
 *
 * @version 1.0 2022-23-06
 * @since 1.0
 */
public class VoiceEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(VoiceEventListener.class);

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + VoiceEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
		Member self = event.getGuild().getSelfMember();
		GuildVoiceState selfVoiceState = self.getVoiceState();

		if (selfVoiceState == null)
			return;

		if (!selfVoiceState.inAudioChannel())
			return;

		if (event.getChannelLeft() != selfVoiceState.getChannel())
			return;

		int numPeople = 0;
		for (Member member : event.getChannelLeft().getMembers()) {
			if (!member.getUser().isBot())
				numPeople++;
		}

		if (numPeople > 0)
			return;

		event.getGuild().getAudioManager().closeAudioConnection();
	}
}
