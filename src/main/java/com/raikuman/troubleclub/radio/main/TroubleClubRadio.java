package com.raikuman.troubleclub.radio.main;

import com.raikuman.botutilities.crypto.keystore.KeyStoreManager;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.troubleclub.radio.config.keystore.TCRadioKeyStore;
import com.raikuman.troubleclub.radio.config.music.MusicConfig;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistConfig;
import com.raikuman.troubleclub.radio.config.member.MemberConfig;
import com.raikuman.troubleclub.radio.config.member.MemberDB;
import com.raikuman.troubleclub.radio.listener.InvokeData;
import com.raikuman.botutilities.configs.ConfigFileWriter;
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * The main class for the bot
 *
 * @version 1.8 2023-22-06
 * @since 1.0
 */
public class TroubleClubRadio {
	private static final Logger logger = LoggerFactory.getLogger(TroubleClubRadio.class);

	public static void main(String[] args) {
		ConfigFileWriter.handleConfigs(true, new MusicConfig());

		KeyStoreManager.initializeKeyStore(
			List.of(
				new TCRadioKeyStore()
			)
		);

		List<GatewayIntent> gatewayIntents = Arrays.asList(
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.MESSAGE_CONTENT
		);

		JDA jda = buildJDA(gatewayIntents);

		if (jda == null) {
			logger.info("Could not create JDA object" );
			return;
		}

		logger.info("Successfully created JDA object");

		try {
			jda.awaitStatus(JDA.Status.CONNECTED);
			logger.info("Bot connected to Discord");
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.info("Bot could not connect to Discord");
		}

		setDatabase(jda);
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
				.addEventListeners(InvokeData.provideListeners())
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setMaxReconnectDelay(32)
				.setAutoReconnect(true)
				.setRequestTimeoutRetry(true)
				.build();

			setPresence(jda);
			setAvatarPicture(jda);
		} catch (IllegalArgumentException e) {
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

	/**
	 * Handle database methods
	 * @param jda The jda object to set database with
	 */
	private static void setDatabase(JDA jda) {
		DatabaseManager.executeConfigStatements(List.of(
			new MusicConfig(),
			new MemberConfig(),
			new PlaylistConfig()
		));

		MemberDB.populateMemberTable(jda.getGuilds());
	}
}
