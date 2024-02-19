package com.raikuman.troubleclub.radio.commands.playlist.mixplaylist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.invokes.context.SelectContext;
import com.raikuman.botutilities.invokes.interfaces.SelectInterface;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.time.Duration;

/**
 * Handles mixing two playlists using a user's playlist and another user's playlist by adding the second playlist to
 * the beginning of the first playlist
 *
 * @version 1.0 2023-27-07
 * @since 1.3
 */
public class AddToFrontPlaylist implements SelectInterface {

    public Triple<String, Integer, Integer> userPlaylist;
    public Triple<String, Integer, Integer> targetPlaylist;
    public EmbedBuilder builder;

    @Override
    public void handle(SelectContext ctx) {
        // Handle mixing playlist
        if (!PlaylistDB.mixPlaylists(ctx.getEventMember().getUser(), userPlaylist, targetPlaylist, PlaylistMixEnum.ADD_TO_FRONT)) {
            MessageResources.timedMessage(
                "There was an error mixing these cassette",
                ctx.getEvent().getChannel().asTextChannel(),
                5
            );
            return;
        }

        // Handle updating embed
        MessageEditCallbackAction callbackAction = ctx.getEvent().deferEdit();

        builder
            .setAuthor("\uD83D\uDCFC Created Mixtape", null, ctx.getEventMember().getEffectiveAvatarUrl());

        StringBuilder descriptionBuilder = builder.getDescriptionBuilder();
        descriptionBuilder.setLength(0);
        descriptionBuilder
            .append("Successfully mixed these cassettes via add to front:\n\n**")
            .append(targetPlaylist.getFirst())
            .append(" `")
            .append(targetPlaylist.getSecond());

        if (targetPlaylist.getSecond() > 1) {
            descriptionBuilder.append(" songs");
        } else {
            descriptionBuilder.append(" song");
        }

        descriptionBuilder
            .append("`**\n\ninto\n\n**")
            .append(userPlaylist.getFirst())
            .append(" `")
            .append(userPlaylist.getSecond());

        if (userPlaylist.getSecond() > 1) {
            descriptionBuilder.append(" songs");
        } else {
            descriptionBuilder.append(" song");
        }

        descriptionBuilder
            .append("`**\n\nfor a total of `")
            .append(userPlaylist.getSecond() + targetPlaylist.getSecond())
            .append(" songs`");

        callbackAction.setComponents();
        callbackAction.setEmbeds(builder.build());
        callbackAction
            .delay(Duration.ofSeconds(7))
            .flatMap(InteractionHook::deleteOriginal)
            .queue();
    }

    @Override
    public String displayLabel() {
        return "Add to the front of the cassette";
    }

    @Override
    public String getInvoke() {
        return "addtofrontplaylist";
    }
}
