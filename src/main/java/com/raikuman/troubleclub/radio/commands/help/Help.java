package com.raikuman.troubleclub.radio.commands.help;

import com.raikuman.botutilities.invokes.context.SlashContext;
import com.raikuman.botutilities.invokes.interfaces.SlashInterface;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.botutilities.pagination.manager.PageComponent;
import com.raikuman.botutilities.pagination.manager.PaginationBuilder;
import com.raikuman.troubleclub.radio.commands.help.selects.MusicSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.OtherSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.PlaylistSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.TrackSelect;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Handles the help slash command
 *
 * @version 1.0 2023-22-06
 * @since 1.3
 */
public class Help extends PageComponent implements SlashInterface {

    private final InvokeProvider invokeProvider;

    public Help(InvokeProvider invokeProvider) {
        this.invokeProvider = invokeProvider;

        pagination = new PaginationBuilder(getInvoke() + "help")
            .setTitle("Radio Command Categories")
            .setItemsPerPage(1)
            .enableLoop(true)
            .enableFirstPageButton(true)
            .setSelectionMenu(
                "View commands in category",
                new MusicSelect(invokeProvider),
                new TrackSelect(invokeProvider),
                new PlaylistSelect(invokeProvider),
                new OtherSelect(invokeProvider))
            .build();
    }

    @Override
    public void handle(SlashContext ctx) {
        pagination.updateEmbeds(HelpUtilities.homePageStrings(ctx, invokeProvider));
        pagination.slashHandle(ctx);
    }

    @Override
    public String getInvoke() {
        return "radio";
    }

    @Override
    public String getDescription() {
        return "Shows commands for music";
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getInvoke(), getDescription());
    }
}
