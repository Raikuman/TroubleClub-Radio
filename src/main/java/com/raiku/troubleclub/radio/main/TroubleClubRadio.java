package com.raiku.troubleclub.radio.main;

import com.raiku.troubleclub.radio.config.MusicConfig;
import com.raiku.troubleclub.radio.listener.ListenerHandler;
import com.raikuman.botutilities.configs.ConfigFileWriter;
import com.raikuman.botutilities.configs.EnvLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;

public class TroubleClubRadio {
	private static final Logger logger = LoggerFactory.getLogger(TroubleClubRadio.class);

	public static void main(String[] args) {
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
				.build();
		} catch (IllegalArgumentException | LoginException e) {
			e.printStackTrace();
		}

		return jda;
	}

	private static void writeConfigs() {
		ConfigFileWriter.writeConfigFiles(new MusicConfig());
	}
}
