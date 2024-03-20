package com.raikuman.troubleclub.radio.invoke.playlist.createplaylist;

import com.raikuman.botutilities.invocation.type.ModalComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.troubleclub.radio.invoke.music.Play;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playerhandler.PlaylistCreatorHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.time.Duration;

public class CreatePlaylistLinkModal extends ModalComponent {

    @Override
    public void handle(ModalInteractionEvent ctx) {
        if (ctx.getGuild() == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "An error occurred while trying to get the guild from your interaction.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        Message message = ctx.getMessage();
        if (message == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "An error occurred while trying to get your original message.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            ctx.deferEdit().queue();
            return;
        }

        // Retrieve name
        ModalMapping nameMapping = ctx.getValue("name");
        if (nameMapping == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "An error occurred while trying to get the name of your cassette.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            ctx.deferEdit().queue();
            return;
        }

        // Retrieve link
        ModalMapping linkMapping = ctx.getValue("link");
        if (linkMapping == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "An error occurred while trying to get the link of your cassette.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            ctx.deferEdit().queue();
            return;
        }

        String name = nameMapping.getAsString(), link = linkMapping.getAsString();

        // Check link
        if (!Play.isUrl(link)) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "You must provide a link to create your cassette.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            ctx.deferEdit().queue();
            return;
        }

        if (!link.contains("youtube.com/playlist?list")) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "You must provide a valid playlist link to create your cassette.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            ctx.deferEdit().queue();
            return;
        }

        String playlistName = name;
        if (playlistName.isEmpty()) {
            playlistName = "";
        }

        MusicManager.getInstance().create(new PlaylistCreatorHandler(
            ctx,
            link,
            playlistName));

    }

    @Override
    public String getInvoke() {
        return "createplaylistlinkmodal";
    }

    @Override
    public Modal getModal() {
        TextInput name = TextInput.create("name", "Name (Leave empty to use playlist name)", TextInputStyle.SHORT)
            .setRequired(false).build();

        TextInput link = TextInput.create("link", "Playlist Link", TextInputStyle.SHORT).build();

        return Modal.create(getInvoke(), "Record Cassette with Link")
            .addComponents(ActionRow.of(name), ActionRow.of(link)).build();
    }
}
