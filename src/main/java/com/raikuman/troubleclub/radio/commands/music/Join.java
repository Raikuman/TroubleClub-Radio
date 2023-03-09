package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;

/**
 * Handles the bot joining the voice channel of a user
 *
 * @version 1.5 2023-08-03
 * @since 1.1
 */
public class Join implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		boolean joined = joinChannel(ctx);

		if (joined)
			ctx.getEvent().getMessage().addReaction(Emoji.fromUnicode("U+1F197")).queue();
		else
			ctx.getEvent().getMessage().addReaction(Emoji.fromUnicode("U+1F6AB")).queue();
	}

	@Override
	public String getInvoke() {
		return "join";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Joins the user's voice channel";
	}

	@Override
	public List<String> getAliases() {
		return List.of("j");
	}

	/**
	 * Attempts to join the invoker's channel
	 * @param ctx Provides command context
	 * @return If joined channel
	 */
	public boolean joinChannel(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();
		final Member self = ctx.getGuild().getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		if (selfVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return false;
		}

		GuildVoiceState memberVoiceState = ctx.getEventMember().getVoiceState();
		if (memberVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return false;
		}

		if (!memberVoiceState.inAudioChannel() || (memberVoiceState.getGuild() != ctx.getGuild())) {
			MessageResources.timedMessage(
				"You must be in a voice channel to use this command",
				channel,
				5
			);
			return false;
		}

		if (selfVoiceState.inAudioChannel() && (selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
			if (selfVoiceState.getChannel() == null) {
				MessageResources.connectError(channel, 5);
				return false;
			}

			MessageResources.timedMessage(
				"I'm already in a voice channel: `" + selfVoiceState.getChannel().getName() + "`",
				channel,
				5
			);
			return false;
		}

		if (!self.hasPermission(Permission.VOICE_CONNECT)) {
			if (memberVoiceState.getChannel() == null) {
				MessageResources.connectError(channel, 5);
				return false;
			}

			MessageResources.timedMessage(
				"I don't have permission to join `" + memberVoiceState.getChannel().toString() + "`",
				channel,
				5
			);
			return false;
		}

		ctx.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
		ctx.getGuild().getAudioManager().setSelfDeafened(true);

		return true;
	}

	@Override
	public CategoryInterface getCategory() {
		return new MusicCategory();
	}
}
