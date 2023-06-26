package com.raikuman.troubleclub.radio.commands.other.trello.submitbug;

import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.botutilities.invokes.components.modals.ModalInput;
import com.raikuman.botutilities.invokes.components.modals.ModalInputBuilder;
import com.raikuman.botutilities.invokes.context.ModalContext;
import com.raikuman.botutilities.invokes.interfaces.ModalInterface;
import com.raikuman.troubleclub.radio.api.trello.TrelloHandler;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.util.List;

/**
 * Handles the modal for submitting a bug to a specified Trello board
 *
 * @version 1.0 2023-25-06
 * @since 1.2
 */
public class SubmitBugModal implements ModalInterface {

    @Override
    public void handle(ModalContext ctx) {
        ctx.getEvent().reply("Thank you for your bug report!").setEphemeral(true).queue();

        if (ctx.modalValues().size() != 2) return;

        TrelloHandler.createCard(
            EnvLoader.get("trellolistbug"),
            EnvLoader.get("trellolabelbug"),
            ctx.modalValues().get(0).getAsString(),
            ctx.modalValues().get(1).getAsString()
        );
    }

    @Override
    public String getTitle() {
        return "Report Bug/Issue";
    }

    @Override
    public List<ModalInput> getInputs() {
        return List.of(
            new ModalInputBuilder("Bug/Issue", TextInputStyle.SHORT)
                .setPlaceholder("Bug/issue encountered")
                .setMinLength(4)
                .setMaxLength(50)
                .setRequired(true)
                .build(),
            new ModalInputBuilder("Description", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Description of bug (if needed)")
                .setMinLength(0)
                .setMaxLength(1000)
                .build()
        );
    }

    @Override
    public String getInvoke() {
        return "radio-bug";
    }
}
