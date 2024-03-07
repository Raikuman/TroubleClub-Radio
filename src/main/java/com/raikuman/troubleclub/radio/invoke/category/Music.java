package com.raikuman.troubleclub.radio.invoke.category;

import com.raikuman.botutilities.invocation.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class Music implements Category {

    @Override
    public String getCategory() {
        return "music";
    }

    @Override
    public Emoji getEmoji() {
        return Emoji.fromFormatted("\uD83C\uDFB5");
    }
}
