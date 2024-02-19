package com.raikuman.troubleclub.radio.main;

import com.raikuman.botutilities.BotUtilsSetup;
import com.raikuman.troubleclub.radio.config.music.MusicConfig;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistConfig;
import com.raikuman.troubleclub.radio.listener.InvokeData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The main class for the bot
 *
 * @version 1.10 2023-29-06
 * @since 1.0
 */
public class TroubleClubRadio {
	private static final Logger logger = LoggerFactory.getLogger(TroubleClubRadio.class);

	public static void main(String[] args) {
		JDA jda = BotUtilsSetup.setupJDA(getJDABuilder(
			List.of(
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.MESSAGE_CONTENT)), InvokeData.getManager())
			.setConfigs(new MusicConfig())
			.setDatabases(new MusicConfig(), new PlaylistConfig())
			.build();

		setPresence(jda);
	}

	/**
	 * Sets up a JDA Builder
	 * @param gatewayIntents The list of gateway intents
	 * @return The JDABuilder object
	 */
	private static JDABuilder getJDABuilder(List<GatewayIntent> gatewayIntents) {
		JDABuilder jdaBUilder = null;

		try {
			jdaBUilder = JDABuilder
				.create(gatewayIntents)
				.setChunkingFilter(ChunkingFilter.ALL)
				.enableCache(CacheFlag.VOICE_STATE)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setMaxReconnectDelay(32)
				.setAutoReconnect(true)
				.setRequestTimeoutRetry(true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return jdaBUilder;
	}

	/**
	 * Sets the presence of the bot
	 * @param jda The jda object to set presence for
	 */
	private static void setPresence(JDA jda) {
		jda.getPresence().setPresence(
			OnlineStatus.ONLINE,
			Activity.playing("some tunes \uD83C\uDFB6 | /radio")
		);
	}

	/**
	 * Sets the avatar of the bot
	 * @param jda The jda object to set avatar for
	 */
	private static void setAvatarPicture(JDA jda) {
		File file = new File("profile.png");

		if (!file.exists()) {
			logger.info("No avatar file found");
			return;
		}

		try {
			Icon icon = Icon.from(file);

			jda.getSelfUser().getManager().setAvatar(icon).queue();
		} catch (IOException e) {
			logger.warn("Could not retrieve icon from file: " + file.getName());
		}
	}
}
