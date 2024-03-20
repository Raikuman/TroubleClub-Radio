package com.raikuman.troubleclub.radio.invoke.playlist.playlist;

import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.troubleclub.radio.database.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playerhandler.playlist.PlayPlaylistHandler;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.Duration;

public class PlaySelect extends SelectComponent {

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

        Playlist playlist = getPlaylist(ctx);
        if (playlist == null) {
            return;
        }

        MusicManager.getInstance().play(new PlayPlaylistHandler(
            ctx,
            playlist));

        MusicManager.getInstance().connect(ctx);
    }

    @Override
    public String getInvoke() {
        return "playlistplayselect";
    }

    @Override
    public String displayLabel() {
        return "Queue";
    }

    public static Playlist getPlaylist(StringSelectInteractionEvent ctx) {
        // Retrieve playlist id
        int playlistId = PlaylistUtils.getPlaylistId(ctx.getMessage().getEmbeds().get(0));
        if (playlistId < 0) {
            ctx.editMessageEmbeds(
                EmbedResources.error(
                    "Could not play your cassette!",
                    "An error occurred when trying to retrieve the id for this cassette.",
                    ctx.getChannel(),
                    ctx.getUser()).build()
            ).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return null;
        }

        // Get playlist by id
        Playlist playlist = PlaylistDatabaseHandler.getPlaylist(ctx.getUser(), playlistId);
        if (playlist == null) {
            ctx.editMessageEmbeds(
                EmbedResources.error(
                    "Could not play your cassette!",
                    "An error occurred when trying to retrieve your cassette.",
                    ctx.getChannel(),
                    ctx.getUser()).build()
            ).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return null;
        }

        return playlist;
    }
}
