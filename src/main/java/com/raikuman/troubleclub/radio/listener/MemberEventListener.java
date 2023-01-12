package com.raikuman.troubleclub.radio.listener;

import com.raikuman.troubleclub.radio.config.member.MemberDB;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an event listener for member joining and leaving
 *
 * @version 1.1 2023-11-01
 * @since 1.2
 */
public class MemberEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(MemberEventListener.class);

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + MemberEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		Member member = event.getMember();

		if (member.getUser().isBot())
			return;

		MemberDB.addMember(member);
	}

	/*
	@Override
	public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
		Member member = event.getMember();

		if (member == null)
			return;

		if (member.getUser().isBot())
			return;

		MemberDB.removeMember(member);
	}
	 */
}
