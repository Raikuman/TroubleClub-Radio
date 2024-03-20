package com.raikuman.troubleclub.radio.invoke;

import com.raikuman.botutilities.defaults.invocation.Help;
import com.raikuman.botutilities.defaults.invocation.Settings;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.invocation.type.Slash;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.invoke.music.*;
import com.raikuman.troubleclub.radio.invoke.playlist.createplaylist.CreatePlaylist;
import com.raikuman.troubleclub.radio.invoke.playlist.deleteplaylist.DeletePlaylist;
import com.raikuman.troubleclub.radio.invoke.playlist.renameplaylist.RenamePlaylist;
import com.raikuman.troubleclub.radio.invoke.track.*;

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
            new Leave(),

            new Track(),
            new Tracks(),
            new PauseTracks(),
            new ResumeTracks(),
            new RewindTracks(),
            new StopTracks(),
            new ClearTracks(),
            new LoopTracks(),
            new RepeatTracks(),
            new PruneTracks(),
            new VolumeTracks(),

            new com.raikuman.troubleclub.radio.invoke.playlist.playlist.Playlist(),
            new CreatePlaylist(),
            new DeletePlaylist(),
            new RenamePlaylist()
//            new AddSongToPlaylist(),
//            new RemoveSongFromPlaylist()
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
                new com.raikuman.troubleclub.radio.invoke.category.Track(),
                new Settings()
            ),
            slashes,
            getCommands()
        ));

        return slashes;
    }
}
