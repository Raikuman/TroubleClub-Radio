package com.raikuman.troubleclub.radio.invoke.playlist.createplaylist;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;

import java.util.List;

public class CreatePlaylist extends Command {

    @Override
    public void handle(CommandContext ctx) {
        List<ButtonComponent> buttons = List.of(
            new CreateFromQueueButton(),
            new CreateFromLinkButton()
        );

        componentHandler.addButtons(ctx.event().getAuthor(), ctx.event().getMessage(), buttons);
        ctx.event().getChannel().sendMessageEmbeds(
            EmbedResources.defaultResponse(
                MusicManager.CASSETTE_COLOR,
                "\uD83D\uDCFC Record a Cassette!",
                "Choose an option to record your cassette.",
                ctx.event().getChannel(),
                ctx.event().getAuthor())
                .setFooter("Track " + MusicManager.getInstance().getMusicManager(ctx.event().getGuild())
                    .getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName())
                .build())
            .setComponents(ComponentBuilder.buildButtons(ctx.event().getAuthor(), buttons)).queue();

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "createcassette";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cc", "createc");
    }

    @Override
    public String getDescription() {
        return "Create a cassette from the current track's playing song and queue, or from a playlist with a link.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Playlist());
    }
}
