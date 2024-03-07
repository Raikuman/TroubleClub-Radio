package com.raikuman.troubleclub.radio.invoke;

import com.raikuman.botutilities.defaults.invocation.Help;
import com.raikuman.botutilities.defaults.invocation.Settings;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.invocation.type.Slash;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.invoke.music.*;

import java.util.ArrayList;
import java.util.List;

public class Invokes {

    public static List<Command> getCommands() {
        return List.of(
            new Play(),
            new PlayNow(),
            new PlayTop(),
            new PlayShuffle(),
            new PlayShuffleTop(),
            new PlayShuffleTopNow(),
            new Pause(),
            new Resume(),
            new Rewind(),
            new Stop(),
            new Clear(),
            new NowPlaying(),
            new Loop(),
            new Repeat(),
            new Skip(),
            new SkipTo(),
            new Remove(),
            new Queue(),
            new Shuffle(),
            new Random(),
            new Prune(),
            new Move(),
            new Volume(),
            new Join(),
            new Leave()
        );
    }

    public static List<Slash> getSlashes() {
        List<Slash> slashes = new ArrayList<>();

        slashes.add(new Help(
            "radio",
            "Shows all commands for the radio!",
            List.of(
                new Music(),
                new Playlist(),
                new Settings()
            ),
            slashes,
            getCommands()
        ));

        return slashes;
    }
}
