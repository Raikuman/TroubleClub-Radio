package com.raikuman.troubleclub.radio.invoke.bot;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playerhandler.music.PlayTopHandler;
import net.dv8tion.jda.api.entities.Member;

public class PlayNowBot extends Command {

    @Override
    public void handle(CommandContext ctx) {
        if (!ctx.event().getAuthor().isBot()) {
            return;
        }

        if (ctx.args().size() != 2) {
            return;
        }

        // First argument is always link
        String link = ctx.args().get(0);

        // Second argument is always target memberId
        String memberId = ctx.args().get(1);

        Member member = ctx.event().getGuild().getMemberById(memberId);
        if (member == null) {
            return;
        }

        // Connect
        MusicManager.getInstance().connect(
            ctx.event(),
            MusicChecking.retrieveMemberVoiceChannel(member));

        MusicManager.getInstance().play(new PlayTopHandler(ctx.event(), link, true));
    }

    @Override
    public String getInvoke() {
        return "playnowbot";
    }

    @Override
    public boolean forBots() {
        return true;
    }
}
