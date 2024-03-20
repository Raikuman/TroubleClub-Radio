package com.raikuman.troubleclub.radio.invoke.playlist.renameplaylist;

import com.raikuman.botutilities.invocation.type.ModalComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.time.Duration;

public class RenamePlaylistModal extends ModalComponent {

    @Override
    public void handle(ModalInteractionEvent ctx) {
        Message message = ctx.getMessage();
        if (message == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not rename your cassette!",
                "An error occurred while trying to get your original message.",
                ctx.getChannel(), ctx.getUser()).build()).delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        // Retrieve new name
        ModalMapping mapping = ctx.getValue("rename");
        if (mapping == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not rename your cassette!",
                "An error occurred while trying to get the new name of your cassette.",
                ctx.getChannel(), ctx.getUser()).build()).delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        String newName = mapping.getAsString();

        // Retrieve playlist id from embed
        MessageEmbed embed = message.getEmbeds().get(0);
        int playlistId = PlaylistUtils.getPlaylistId(embed);

        if (playlistId <= 0) {
            MessageResources.embedDelete(ctx.getChannel(), 10,
                EmbedResources.error(
                    "Could not rename your cassette!",
                    "An error occurred when trying to retrieve the id for this cassette.",
                    ctx.getChannel(),
                    ctx.getUser()));
            return;
        }

        if (!PlaylistDatabaseHandler.renamePlaylist(playlistId, newName)) {
            MessageResources.embedDelete(ctx.getChannel(), 10,
                EmbedResources.error(
                    "Could not rename your cassette!",
                    "An error occurred when trying to rename your cassette in the database.",
                    ctx.getChannel(),
                    ctx.getUser()));
            return;
        }

        // Update embed
        EmbedBuilder embedBuilder = new EmbedBuilder(embed);
        embedBuilder
            .setAuthor("\uD83D\uDCFC Renamed Your Cassette", null, ctx.getUser().getEffectiveAvatarUrl())
            .setTitle(newName);

        // Completed renaming
        ctx.editMessageEmbeds(embedBuilder.build())
            .setComponents()
            .delay(Duration.ofSeconds(10))
            .flatMap(InteractionHook::deleteOriginal)
            .queue();
    }

    @Override
    public String getInvoke() {
        return "renameplaylist";
    }

    @Override
    public Modal getModal() {
        TextInput subject = TextInput.create("rename", "New Name", TextInputStyle.SHORT)
            .setMinLength(3)
            .setMaxLength(20).build();

        return Modal.create(getInvoke(), "Rename Playlist").addComponents(
            ActionRow.of(subject)
        ).build();
    }
}
