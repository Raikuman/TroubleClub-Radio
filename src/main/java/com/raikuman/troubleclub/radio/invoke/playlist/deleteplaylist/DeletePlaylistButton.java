package com.raikuman.troubleclub.radio.invoke.playlist.deleteplaylist;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.time.Duration;
import java.util.List;

public class DeletePlaylistButton extends ButtonComponent {

    @Override
    public void handle(ButtonInteractionEvent ctx) {
        // Retrieve id
        List<MessageEmbed> embeds = ctx.getMessage().getEmbeds();
        if (embeds.size() != 1) {
            MessageResources.embedDelete(ctx.getChannel(), 10,
                EmbedResources.error(
                    "Could not delete cassette!",
                    "An error occurred when trying to delete this cassette.",
                    ctx.getChannel(),
                    ctx.getUser()));
            return;
        }

        MessageEmbed embed = embeds.get(0);
        int playlistId = PlaylistUtils.getPlaylistId(embed);

        if (playlistId <= 0) {
            MessageResources.embedDelete(ctx.getChannel(), 10,
                EmbedResources.error(
                    "Could not delete cassette!",
                    "An error occurred when trying to retrieve the id for this cassette.",
                    ctx.getChannel(),
                    ctx.getUser()));
            return;
        }

        if (PlaylistDatabaseHandler.deletePlaylist(playlistId)) {
            // Update embed
            EmbedBuilder embedBuilder = new EmbedBuilder(embed)
                .setAuthor("\uD83D\uDCFC Deleted Cassette", null,
                    ctx.getInteraction().getUser().getEffectiveAvatarUrl());

            ctx.editMessageEmbeds(embedBuilder.build())
                .setComponents()
                .delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
        }
    }

    @Override
    public String getInvoke() {
        return "deleteplaylistbutton";
    }

    @Override
    public Emoji displayEmoji() {
        return null;
    }

    @Override
    public String displayLabel() {
        return "\uD83D\uDDD1️ Delete cassette";
    }

    @Override
    public ButtonStyle buttonStyle() {
        return ButtonStyle.DANGER;
    }
}
