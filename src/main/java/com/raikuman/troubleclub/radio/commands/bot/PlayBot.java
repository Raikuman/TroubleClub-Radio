package com.raikuman.troubleclub.radio.commands.bot;

import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

/**
 * Handles playing music in a user's voice channel via Trouble Club bot interaction
 *
 * @version 1.1 2023-19-01
 * @since 1.2
 */
public class PlayBot implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		if (ctx.getArgs().size() != 2) {
			return;
		}

		String playlistLink = ctx.getArgs().get(0);
		String userIdTarget = ctx.getArgs().get(1);

		Member targetMember = ctx.getGuild().getMemberById(userIdTarget);
		if (targetMember == null)
			return;

		GuildVoiceState voiceState = targetMember.getVoiceState();
		if (voiceState == null)
			return;

		AudioChannelUnion audioChannelUnion = voiceState.getChannel();
		if (audioChannelUnion == null)
			return;

		VoiceChannel voiceChannel = voiceState.getChannel().asVoiceChannel();
		ctx.getGuild().getAudioManager().openAudioConnection(voiceChannel);

		// Shuffle instead of regular load and play
		PlayerManager.getInstance().loadAndPlay(channel, playlistLink, ctx.getEvent().getAuthor(), ctx.getGuild().getIdLong());

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "playbot";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}
}