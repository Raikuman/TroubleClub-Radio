package com.raikuman.troubleclub.radio.main;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.troubleclub.radio.config.MusicConfig;
import com.raikuman.troubleclub.radio.listener.ListenerHandler;
import com.raikuman.botutilities.configs.ConfigFileWriter;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.configs.EnvLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * The main class for the bot
 *
 * @version 1.3 2022-13-07
 * @since 1.0
 */
public class TroubleClubRadio {
	private static final Logger logger = LoggerFactory.getLogger(TroubleClubRadio.class);

	public static void main(String[] args) {
		DatabaseManager.executeConfigStatements(List.of(new MusicConfig()));

		List<GatewayIntent> gatewayIntents = Arrays.asList(
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES
		);

		JDA jda = buildJDA(gatewayIntents);

		if (jda == null) {
			logger.info("Could not create JDA object");
		} else {
			logger.info("Successfully created JDA object");

			try {
				jda.awaitStatus(JDA.Status.CONNECTED);
				logger.info("Bot connected to Discord");
			} catch (InterruptedException var4) {
				var4.printStackTrace();
				logger.info("Bot could not connect to Discord");
			}

			writeConfigs();
		}
	}

	/**
	 * Builds a jda object with the given gateway intents
	 * @param gatewayIntents The list of gateway intents
	 * @return The jda object
	 */
	private static JDA buildJDA(List<GatewayIntent> gatewayIntents) {
		JDA jda = null;

		try {
			jda = JDABuilder
				.createDefault(EnvLoader.get("token"))
				.enableIntents(gatewayIntents)
				.setChunkingFilter(ChunkingFilter.ALL)
				.enableCache(CacheFlag.VOICE_STATE)
				.addEventListeners(ListenerHandler.getListenerManager().getListeners())
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setMaxReconnectDelay(32)
				.setAutoReconnect(true)
				.setRequestTimeoutRetry(true)
				.build();

			setPresence(jda);
			setAvatarPicture(jda);
		} catch (IllegalArgumentException | LoginException e) {
			e.printStackTrace();
		}

		return jda;
	}

	/**
	 * Sets the presence of the bot
	 * @param jda The jda object to set presence for
	 */
	private static void setPresence(JDA jda) {
		jda.getPresence().setPresence(
			OnlineStatus.ONLINE,
			Activity.playing("music | " + ConfigIO.readConfig("settings", "prefix") + "help")
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

	/**
	 * Writes all config files
	 */
	private static void writeConfigs() {
		ConfigFileWriter.writeConfigFiles(new MusicConfig());
	}
}
