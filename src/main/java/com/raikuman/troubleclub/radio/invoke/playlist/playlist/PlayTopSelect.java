package com.raikuman.troubleclub.radio.invoke.playlist.playlist;

import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playerhandler.playlist.PlayTopPlaylistHandler;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class PlayTopSelect extends SelectComponent {

    @Override
    public void handle(StringSelectInteractionEvent ctx) {
        if (MusicChecking.setup(
                ctx.getGuild(),
                ctx.getChannel(),
                ctx.getMessage(),
                ctx.getMember())
            .checkMemberNotInVoiceChannel(true)
            .checkBotInDifferentVoiceChannel(true, false)
            .checkLacksPermission(true)
            .check()) {
            return;
        }

        Playlist playlist = PlaySelect.getPlaylist(ctx);
        if (playlist == null) {
            return;
        }

        MusicManager.getInstance().play(new PlayTopPlaylistHandler(
            ctx,
            playlist));

        MusicManager.getInstance().connect(
            ctx,
            MusicChecking.retrieveMemberVoiceChannel(ctx.getMember()));
    }

    @Override
    public String getInvoke() {
        return "playlistplaytopselect";
    }

    @Override
    public String displayLabel() {
        return "Queue to Top";
    }
}
