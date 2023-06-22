package com.raikuman.troubleclub.radio.commands.help.selects;

import com.raikuman.botutilities.invokes.context.SelectContext;
import com.raikuman.botutilities.invokes.interfaces.SelectInterface;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.botutilities.pagination.manager.PageComponent;
import com.raikuman.botutilities.pagination.manager.PaginationBuilder;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.commands.help.HelpUtilities;

/**
 * Handles the track select for the help command
 *
 * @version 1.0 2023-22-06
 * @since 1.3
 */
public class TrackSelect extends PageComponent implements SelectInterface {

    private final InvokeProvider invokeProvider;

    public TrackSelect(InvokeProvider invokeProvider) {
        this.invokeProvider = invokeProvider;

        pagination = new PaginationBuilder(getInvoke())
            .setTitle("Track Commands")
            .setItemsPerPage(1)
            .enableLoop(true)
            .enableFirstPageButton(true)
            .enableHomeButton(true)
            .build();
    }

    @Override
    public void handle(SelectContext ctx) {
        pagination.updateEmbeds(HelpUtilities.categoryPageStrings(ctx, invokeProvider, new TrackCategory()));
        pagination.selectHandle(ctx);
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
