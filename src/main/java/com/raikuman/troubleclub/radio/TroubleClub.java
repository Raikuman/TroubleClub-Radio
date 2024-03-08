package com.raikuman.troubleclub.radio;

import com.raikuman.botutilities.BotSetup;
import com.raikuman.troubleclub.radio.config.music.MusicConfig;
import com.raikuman.troubleclub.radio.config.music.MusicStartup;
import com.raikuman.troubleclub.radio.config.PlaylistStartup;
import com.raikuman.troubleclub.radio.database.music.MusicListener;
import com.raikuman.troubleclub.radio.invoke.Invokes;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.List;

public class TroubleClub {

    public static void main(String[] args) {
        JDABuilder jdaBuilder = JDABuilder
            .create(List.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.MESSAGE_CONTENT
            ))
            .setChunkingFilter(ChunkingFilter.ALL)
            .enableCache(CacheFlag.VOICE_STATE)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setMaxReconnectDelay(32)
            .setAutoReconnect(true)
            .setRequestTimeoutRetry(true);

        JDA jda = BotSetup
            .setup(jdaBuilder)
            .setConfigs(new MusicConfig())
            .setDatabases(new MusicStartup(), new PlaylistStartup())
            .addListeners(List.of(new MusicListener()))
            .addCommands(Invokes.getCommands())
            .addSlashes(Invokes.getSlashes())
            .build(System.getenv("RADIOTOKEN"));

        jda.getPresence().setActivity(Activity.playing("music! \uD83C\uDFB9 | /radio"));
    }
}
