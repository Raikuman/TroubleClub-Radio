package com.raikuman.troubleclub.radio.listener;

import com.raikuman.troubleclub.radio.database.MusicDatabaseHandler;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MusicEventListener.class);

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + MusicEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        MusicDatabaseHandler.addGuildSettings(event.getGuild());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        MusicDatabaseHandler.removeGuildSettings(event.getGuild());
    }
}
