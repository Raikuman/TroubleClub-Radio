package com.raikuman.troubleclub.radio.commands.help.selects;

import com.raikuman.botutilities.invokes.context.SelectContext;
import com.raikuman.botutilities.invokes.interfaces.SelectInterface;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.botutilities.pagination.manager.PageComponent;
import com.raikuman.botutilities.pagination.manager.PaginationBuilder;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.commands.help.HelpUtilities;

/**
 * Handles the music select for the help command
 *
 * @version 1.0 2023-22-06
 * @since 1.3
 */
public class MusicSelect extends PageComponent implements SelectInterface {

    private final InvokeProvider invokeProvider;

    public MusicSelect(InvokeProvider invokeProvider) {
        this.invokeProvider = invokeProvider;

        pagination = new PaginationBuilder(getInvoke())
            .setTitle("Music Commands")
            .setItemsPerPage(1)
            .enableLoop(true)
            .enableFirstPageButton(true)
            .enableHomeButton(true)
            .build();
    }

    @Override
    public void handle(SelectContext ctx) {
        pagination.updateEmbeds(HelpUtilities.categoryPageStrings(ctx, invokeProvider, new MusicCategory()));
        pagination.selectHandle(ctx);
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
