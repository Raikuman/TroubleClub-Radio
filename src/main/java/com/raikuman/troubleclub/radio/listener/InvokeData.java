package com.raikuman.troubleclub.radio.listener;

import com.raikuman.botutilities.invokes.interfaces.ButtonInterface;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.botutilities.invokes.interfaces.SlashInterface;
import com.raikuman.botutilities.invokes.manager.InvokeManager;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.troubleclub.radio.commands.help.Help;
import com.raikuman.troubleclub.radio.commands.music.*;
import com.raikuman.troubleclub.radio.commands.other.ChangePrefix;
import com.raikuman.troubleclub.radio.commands.other.Changelog;
import com.raikuman.troubleclub.radio.commands.other.ToS;
import com.raikuman.troubleclub.radio.commands.other.trello.RequestFeature;
import com.raikuman.troubleclub.radio.commands.other.trello.SubmitBug;
import com.raikuman.troubleclub.radio.commands.playlist.*;
import com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist.CancelDeletePlaylist;
import com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist.ConfirmDeletePlaylist;
import com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist.DeletePlaylist;
import com.raikuman.troubleclub.radio.commands.track.*;

import java.util.Arrays;
import java.util.List;

/**
 * Provides instantiating all invokes and providing them to the InvokeManager to create listeners
 *
 * @version 1.0 2023-22-06
 * @since 1.3
 */
public class InvokeData {

    public static Object[] provideListeners() {
        InvokeProvider invokeProvider = new InvokeProvider();
        invokeProvider.commands.addAll(provideCommands());
        invokeProvider.slashes.addAll(provideSlashes());
        invokeProvider.buttons.addAll(provideButtons());

        Help help = new Help(invokeProvider);
        invokeProvider.slashes.add(help);

        return new InvokeManager(
            invokeProvider,
            new VoiceEventListener(),
            new MemberEventListener()
        ).getListeners();
    }

    private static List<CommandInterface> provideCommands() {
        return Arrays.asList(
            new Clear(),
            new Join(),
            new Leave(),
            new Loop(),
            new Move(),
            new NowPlaying(),
            new Pause(),
            new Play(),
            new PlayNow(),
            new PlayTop(),
            new Prune(),
            new Queue(),
            new Random(),
            new Remove(),
            new Repeat(),
            new Resume(),
            new Rewind(),
            new Shuffle(),
            new ShufflePlay(),
            new Skip(),
            new SkipTo(),
            new Stop(),
            new Volume(),

            new ChangeTrack(),
            new ClearTracks(),
            new LoopTracks(),
            new PauseTracks(),
            new PlayingTracks(),
            new PruneTracks(),
            new RepeatTracks(),
            new ResumeTracks(),
            new RewindTracks(),
            new StopTracks(),
            new VolumeTracks(),

            new DeletePlaylist(),
            new CreatePlaylist(),
            new Playlist(),
            new PlayPlaylist(),
            new RenamePlaylist(),
            new ShufflePlayPlaylist(),

            new Changelog(),
            new ChangePrefix(),
            new ToS()
        );
    }

    private static List<SlashInterface> provideSlashes() {
        return Arrays.asList(
            new RequestFeature(),
            new SubmitBug()
        );
    }

    private static List<ButtonInterface> provideButtons() {
        return Arrays.asList(
            new CancelDeletePlaylist(),
            new ConfirmDeletePlaylist()
        );
    }
}
