package com.raikuman.troubleclub.radio.commands.help.selects;

import com.raikuman.botutilities.invokes.components.components.PaginationComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.components.pagination.PaginationBuilder;
import com.raikuman.botutilities.invokes.context.SelectContext;
import com.raikuman.botutilities.invokes.interfaces.SelectInterface;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.commands.help.HelpUtilities;

/**
 * Handles the music select for the help command
 *
 * @version 1.2 2023-16-09
 * @since 1.3
 */
public class MusicSelect extends ComponentInvoke implements SelectInterface {

    private final InvokeProvider invokeProvider;

    public MusicSelect(InvokeProvider invokeProvider) {
        this.invokeProvider = invokeProvider;

        componentHandler = ComponentHandler.pagination(new PaginationComponent(
            new PaginationBuilder(getInvoke())
                .setTitle("Music Commands")
                .setItemsPerPage(1)
                .enableLoop(true)
                .enableFirstPageButton(true)
                .build()
        ));
    }

    @Override
    public void handle(SelectContext ctx) {
        componentHandler.providePaginationComponent().updateItems(ctx.getEventMember(),
            HelpUtilities.categoryPageStrings(ctx, invokeProvider, new MusicCategory()));
        componentHandler.providePaginationComponent().handleContext(ctx);
    }

    @Override
    public String displayLabel() {
        return "Music Commands";
    }

    @Override
    public String getInvoke() {
        return "helpmusic";
    }
}
