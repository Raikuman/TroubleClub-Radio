package com.raikuman.troubleclub.radio.commands.playlist.mixplaylist;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.components.components.SelectMenuComponent;
import com.raikuman.botutilities.invokes.components.components.rows.MenuRow;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.config.playlist.PlaylistDB;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.Duration;
import java.util.List;

/**
 * Handles mixing two playlists using a user's playlist and another user's playlist
 *
 * @version 1.0 2023-27-07
 * @since 1.3
 */
public class MixPlaylist extends ComponentInvoke implements CommandInterface {

    private final ShuffleToPlaylist shuffleToPlaylist;
    private final AddToBackPlaylist addToBackPlaylist;
    private final AddToFrontPlaylist addToFrontPlaylist;

    public MixPlaylist() {
        shuffleToPlaylist = new ShuffleToPlaylist();
        addToBackPlaylist = new AddToBackPlaylist();
        addToFrontPlaylist = new AddToFrontPlaylist();

        componentHandler = ComponentHandler.selectMenu(
            new SelectMenuComponent(getInvoke(),
                new MenuRow("Method to mix",
                    shuffleToPlaylist, addToBackPlaylist, addToFrontPlaylist
                )
            )
        );
    }

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel().asTextChannel();

        if (ctx.getArgs().size() > 4 || ctx.getArgs().size() < 1) {
            MessageResources.timedMessage(
                "You must provide a valid argument for this command: `" + getUsage() + "`",
                channel,
                5
            );
            return;
        }

        // Check if first arg is a number
        Triple<String, Integer, Integer> userPlaylist = checkUserPlaylistNum(ctx.getArgs().get(0), ctx.getEventMember().getUser());

        // Check if second arg is a user
        User targetUser = null;
        List<Member> mentions = ctx.getEvent().getMessage().getMentions().getMembers();
        if (mentions.size() == 1) {
            targetUser = mentions.get(0).getUser();
        }

        Triple<String, Integer, Integer> targetPlaylist;
        if (targetUser == null) {
            // Check if second arg is a number
            targetPlaylist = checkUserPlaylistNum(ctx.getArgs().get(1), ctx.getEventMember().getUser());
            targetUser = ctx.getEventMember().getUser();
        } else {
            // Check if third arg is a number
            targetPlaylist = checkUserPlaylistNum(ctx.getArgs().get(2), targetUser);
        }

        if (userPlaylist == null || targetPlaylist == null) {
            MessageResources.timedMessage(
                "You must provide a valid argument for this command: `" + getUsage() + "`",
                channel,
                5
            );
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
            .setColor(RandomColor.getRandomColor())
            .setAuthor("\uD83D\uDCFC Mixtaping", null, ctx.getEventMember().getEffectiveAvatarUrl());

        updateSelectMenu(userPlaylist, targetPlaylist, builder);

        StringBuilder descriptionBuilder = builder.getDescriptionBuilder();
        descriptionBuilder
            .append("Please select a method to mix these cassettes:\n\n*")
            .append(targetUser.getEffectiveName())
            .append("'s cassette*\n**")
            .append(targetPlaylist.getFirst())
            .append(" `")
            .append(targetPlaylist.getSecond());

        if (targetPlaylist.getSecond() > 1) {
            descriptionBuilder.append(" songs");
        } else {
            descriptionBuilder.append(" song");
        }

        descriptionBuilder
            .append("`**\n\nis being mixed with\n\n")
            .append("*Your cassette*\n**")
            .append(userPlaylist.getFirst())
            .append(" `")
            .append(userPlaylist.getSecond());

        if (userPlaylist.getSecond() > 1) {
            descriptionBuilder.append(" songs");
        } else {
            descriptionBuilder.append(" song");
        }

        descriptionBuilder
            .append("`**");

        ctx.getChannel().sendMessageEmbeds(builder.build())
            .setComponents(componentHandler.asActionRows(ctx))
            .delay(Duration.ofSeconds(25))
            .flatMap((message) -> message.editMessageEmbeds(updateEmbed(builder, ctx.getEventMember().getEffectiveAvatarUrl()).build()))
            .flatMap(Message::editMessageComponents)
            .delay(Duration.ofSeconds(7))
            .flatMap(Message::delete)
            .queue(null, new ErrorHandler()
                .ignore(ErrorResponse.UNKNOWN_MESSAGE));

        ctx.getEvent().getMessage().delete().queue();
    }

    private EmbedBuilder updateEmbed(EmbedBuilder embedBuilder, String avatarUrl) {
        embedBuilder
            .setAuthor("\uD83D\uDCFC The selected cassettes will not be mixed!",
                null,
                avatarUrl);

        embedBuilder.setDescription("");

        return embedBuilder;
    }

    @Override
    public String getInvoke() {
        return "mixtape";
    }

    @Override
    public String getUsage() {
        return "<your cassette #> <@user> <their cassette #>";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of(
            "mix",
            "mpl",
            "mixcassette",
            "mixplaylist",
            "mcass"
        );
    }

    @Override
    public CategoryInterface getCategory() {
        return new PlaylistCategory();
    }

    /**
     * Update the select menu objects with playlist information
     * @param userPlaylist The user's playlist info
     * @param targetPlaylist The target's playlist info
     * @param builder The builder for mixing
     */
    private void updateSelectMenu(Triple<String, Integer, Integer> userPlaylist,
                                  Triple<String, Integer, Integer> targetPlaylist, EmbedBuilder builder) {
        shuffleToPlaylist.userPlaylist = userPlaylist;
        shuffleToPlaylist.targetPlaylist = targetPlaylist;
        shuffleToPlaylist.builder = builder;

        addToBackPlaylist.userPlaylist = userPlaylist;
        addToBackPlaylist.targetPlaylist = targetPlaylist;
        addToBackPlaylist.builder = builder;

        addToFrontPlaylist.userPlaylist = userPlaylist;
        addToFrontPlaylist.targetPlaylist = targetPlaylist;
        addToFrontPlaylist.builder = builder;
    }

    /**
     * Retrieves the playlist info from the user's input
     * @param input The input to get the playlist from
     * @param user The user to get the playlist from
     * @return The user's playlist info
     */
    private Triple<String, Integer, Integer> checkUserPlaylistNum(String input, User user) {
        List<Triple<String, Integer, Integer>> userPlaylists = PlaylistDB.getBasicPlaylistInfo(user);
        int userPlaylist = -1;
        try {
            userPlaylist = Integer.parseInt(input);
            if (userPlaylist > userPlaylists.size()) {
                userPlaylist = -1;
            }
        } catch (NumberFormatException e) {
            for (int i = 0; i < userPlaylists.size(); i++) {
                if (input.equalsIgnoreCase(userPlaylists.get(i).getFirst())) {
                    userPlaylist = i + 1;
                }
            }
        }

        if (userPlaylist == -1) {
            return null;
        }

        return userPlaylists.get(userPlaylist - 1);
    }
}
