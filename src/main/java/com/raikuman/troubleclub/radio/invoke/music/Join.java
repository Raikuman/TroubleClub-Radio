package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;

public class Join extends Command {

    @Override
    public void handle(CommandContext ctx) {
        // Check if member is in a voice channel
        if (MusicChecking.isMemberNotInVoiceChannel(ctx)) {
            return;
        }

        // Check if bot is in a voice channel that is not the member's
        if (MusicChecking.isBotInDifferentVoiceChannel(ctx, false)) {
            return;
        }

        // Check if the bot has permissions to join
        if (MusicChecking.lacksPermissionToJoin(ctx)) {
            return;
        }

        // Connect
        MusicManager.getInstance().connect(ctx);
        ctx.event().getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDC4D")).queue();
    }

    @Override
    public String getInvoke() {
        return "join";
    }

    @Override
    public List<String> getAliases() {
        return List.of("j");
    }

    @Override
    public String getDescription() {
        return "Joins a user's voice channel.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}