package com.raikuman.troubleclub.radio.commands.other.trello.requestfeature;

import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.botutilities.invokes.components.modals.ModalInput;
import com.raikuman.botutilities.invokes.components.modals.ModalInputBuilder;
import com.raikuman.botutilities.invokes.context.ModalContext;
import com.raikuman.botutilities.invokes.interfaces.ModalInterface;
import com.raikuman.troubleclub.radio.api.trello.TrelloHandler;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.util.List;

/**
 * Handles the modal for requesting a feature to a specified Trello board
 *
 * @version 1.0 2023-25-06
 * @since 1.2
 */
public class RequestFeatureModal implements ModalInterface {

    @Override
    public void handle(ModalContext ctx) {
        ctx.getEvent().reply("Thank you for your request!").setEphemeral(true).queue();

        if (ctx.modalValues().size() != 2) return;

        TrelloHandler.createCard(
            EnvLoader.get("trellolistfeatures"),
            EnvLoader.get("trellolabelfeatures"),
            ctx.modalValues().get(0).getAsString(),
            ctx.modalValues().get(1).getAsString()
        );
    }

    @Override
    public String getTitle() {
        return "Request Feature";
    }

    @Override
    public List<ModalInput> getInputs() {
        return List.of(
            new ModalInputBuilder("Feature", TextInputStyle.SHORT)
                .setPlaceholder("Feature requested")
                .setMaxLength(4)
                .setMaxLength(50)
                .build(),
            new ModalInputBuilder("Description", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Feature requested")
                .setMaxLength(0)
                .setMaxLength(1000)
                .setRequired(false)
                .build()
        );
    }

    @Override
    public String getInvoke() {
        return "radio-feature";
    }
}
