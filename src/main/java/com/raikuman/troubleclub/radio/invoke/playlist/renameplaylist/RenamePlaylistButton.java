package com.raikuman.troubleclub.radio.invoke.playlist.renameplaylist;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.ModalComponent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class RenamePlaylistButton extends ButtonComponent {

    @Override
    public void handle(ButtonInteractionEvent ctx) {
        ModalComponent modalComponent = new RenamePlaylistModal();
        componentHandler.addModal(modalComponent);

        ctx.replyModal(modalComponent.getModal()).queue();
    }

    @Override
    public String getInvoke() {
        return "testInteraction";
    }

    @Override
    public Emoji displayEmoji() {
        return Emoji.fromFormatted("\uD83D\uDD21");
    }

    @Override
    public String displayLabel() {
        return "Rename cassette";
    }

    @Override
    public ButtonStyle buttonStyle() {
        return ButtonStyle.SUCCESS;
    }
}
