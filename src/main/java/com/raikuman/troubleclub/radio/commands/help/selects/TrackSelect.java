package com.raikuman.troubleclub.radio.commands.help.selects;

import com.raikuman.botutilities.invokes.components.components.PaginationComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.components.pagination.PaginationBuilder;
import com.raikuman.botutilities.invokes.context.SelectContext;
import com.raikuman.botutilities.invokes.interfaces.SelectInterface;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.commands.help.HelpUtilities;

/**
 * Handles the track select for the help command
 *
 * @version 1.2 2023-16-09
 * @since 1.3
 */
public class TrackSelect extends ComponentInvoke implements SelectInterface {

    private final InvokeProvider invokeProvider;

    public TrackSelect(InvokeProvider invokeProvider) {
        this.invokeProvider = invokeProvider;

        componentHandler = ComponentHandler.pagination(new PaginationComponent(
            new PaginationBuilder(getInvoke())
                .setTitle("Track Commands")
                .setItemsPerPage(1)
                .enableLoop(true)
                .enableFirstPageButton(true)
                .build()
        ));
    }

    @Override
    public void handle(SelectContext ctx) {
        componentHandler.providePaginationComponent().updateItems(ctx.getEventMember(),
            HelpUtilities.categoryPageStrings(ctx, invokeProvider, new TrackCategory()));
        componentHandler.providePaginationComponent().handleContext(ctx);
    }

    @Override
    public String displayLabel() {
        return "Track Commands";
    }

    @Override
    public String getInvoke() {
        return "helptrack";
    }
}
