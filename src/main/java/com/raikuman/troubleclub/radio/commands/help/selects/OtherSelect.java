package com.raikuman.troubleclub.radio.commands.help.selects;

import com.raikuman.botutilities.invokes.components.components.PaginationComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.components.pagination.PaginationBuilder;
import com.raikuman.botutilities.invokes.context.SelectContext;
import com.raikuman.botutilities.invokes.interfaces.SelectInterface;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import com.raikuman.troubleclub.radio.commands.help.HelpUtilities;

/**
 * Handles the other select for the help command
 *
 * @version 1.1 2023-25-06
 * @since 1.3
 */
public class OtherSelect extends ComponentInvoke implements SelectInterface {

    private final InvokeProvider invokeProvider;

    public OtherSelect(InvokeProvider invokeProvider) {
        this.invokeProvider = invokeProvider;

        componentHandler = ComponentHandler.pagination(new PaginationComponent(
            new PaginationBuilder(getInvoke())
                .setTitle("Other Commands")
                .setItemsPerPage(1)
                .enableLoop(true)
                .enableFirstPageButton(true)
                .build()
        ));
    }

    @Override
    public void handle(SelectContext ctx) {
        componentHandler.providePaginationComponent().updateItems(
            HelpUtilities.categoryPageStrings(ctx, invokeProvider, new OtherCategory()));
        componentHandler.providePaginationComponent().handleContext(ctx);
    }

    @Override
    public String displayLabel() {
        return "Other Commands";
    }

    @Override
    public String getInvoke() {
        return "helpother";
    }
}
