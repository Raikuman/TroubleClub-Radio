package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.config.MusicConfig;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.helpers.MessageResources;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles setting the volume of the bot
 *
 * @version 1.3 2022-29-06
 * @since 1.0
 */
public class Volume implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getGuild().getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		if (selfVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return;
		}

		GuildVoiceState memberVoiceState = ctx.getEventMember().getVoiceState();
		if (memberVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return;
		}

		if (!memberVoiceState.inAudioChannel()) {
			MessageResources.timedMessage(
				"You must be in a voice channel to use this command",
				channel,
				5
			);
			return;
		}

		if (selfVoiceState.inAudioChannel() && (selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
			if (selfVoiceState.getChannel() == null) {
				MessageResources.connectError(channel, 5);
				return;
			}

			MessageResources.timedMessage(
				"You must be in `" + selfVoiceState.getChannel().getName() + "` to use this command",
				channel,
				5
			);
			return;
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.getAudioPlayer();

		if (ctx.getArgs().size() == 0) {

			String volumeConfig = "volumetrack" + musicManager.getCurrentAudioTrack();

			String volume = ConfigIO.readConfig(new MusicConfig().fileName(), volumeConfig);

			int volumeNum;
			try {
				volumeNum = Integer.parseInt(volume);
			} catch (NumberFormatException e) {
				volumeNum = 100;
			}

			EmbedBuilder builder = new EmbedBuilder()
				.setAuthor(
					"\uD83D\uDD0A Current volume: " + volumeNum + "%",
					null,
					ctx.getEventMember().getEffectiveAvatarUrl()
				)
				.setFooter("Audio track " + musicManager.getCurrentAudioTrack());

			ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
		}

		if (ctx.getArgs().size() > 2) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		if (ctx.getArgs().size() == 1) {
			try {
				int volumeNum = Integer.parseInt(ctx.getArgs().get(0));

				if ((volumeNum < 1) || (volumeNum > 100)) {
					MessageResources.timedMessage(
						"You must enter a volume setting between 1-100",
						channel,
						5
					);
					return;
				}

				int calculateVolume = (int) Math.ceil((double) volumeNum / 4);

				String volumeConfig = "volumetrack" + musicManager.getCurrentAudioTrack();

				ConfigIO.overwriteConfig(
					new MusicConfig().fileName(),
					volumeConfig,
					Integer.toString(volumeNum)
				);

				audioPlayer.setVolume(calculateVolume);

				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor(
						"\uD83D\uDD0A Set volume to " + volumeNum + "%",
						null,
						ctx.getEventMember().getEffectiveAvatarUrl()
					)
					.setFooter("Audio track " + musicManager.getCurrentAudioTrack());

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
				return;
			}
		}

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "volume";
	}

	@Override
	public String getUsage() {
		return "(<1-100>)";
	}

	@Override
	public String getDescription() {
		return "Shows the current volume of the bot or set the volume of the bot";
	}
}
