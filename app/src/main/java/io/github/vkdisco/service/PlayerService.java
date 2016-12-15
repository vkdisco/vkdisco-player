package io.github.vkdisco.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;

import com.un4seen.bass.BASS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.player.Player;
import io.github.vkdisco.player.PlayerState;
import io.github.vkdisco.player.Playlist;
import io.github.vkdisco.player.interfaces.OnPlayerStateChangedListener;
import io.github.vkdisco.player.interfaces.OnPlaylistChangedListener;
import io.github.vkdisco.player.interfaces.OnTrackSwitchListener;

/**
 * PlayerService
 * Contains Player, provides it communication with user,
 * foreground and background work
 */

public class PlayerService extends Service implements OnTrackSwitchListener,
        OnPlayerStateChangedListener, OnPlaylistChangedListener {
    private static final String DEFAULT_PLAYLIST_FILENAME = "default.vkdpls";

    private static final String TAG = "PlayerService";
    public static final String EXTRA_WAKEUP = "io.github.vkdisco.PlayerService.EXTRA.WAKEUP";
    public static final String EXTRA_EVENT = "io.github.vkdisco.PlayerService.EXTRA.EVENT";

    public static final String EXTRA_STATE = "io.github.vkdisco.PlayerService.EXTRA.STATE";

    public static final String BROADCAST_ACTION_EVENT = "io.github.vkdisco.PlayerService.BROADCAST.EVENT";
    public static final String EVENT_STATE_CHANGED = "io.github.vkdisco.PlayerService.EVENT.STATE_CHANGED";
    public static final String EVENT_PLAYLIST_CHANGED = "io.github.vkdisco.PlayerService.EVENT.PLAYLIST_CHANGED";
    public static final String EVENT_TRACK_SWITCHED = "io.github.vkdisco.PlayerService.EVENT.TRACK_SWITCHED";

    private Player player;
    private Playlist playlist;

    //Lifecycle methods
    @Override
    public void onCreate() {
        super.onCreate();
        BASS.BASS_Init(-1, 44100, 0);
        player = new Player();
        player.setStateChangedListener(this);
        player.setTrackSwitchListener(this);
        File playlistFile = new File(getFilesDir(), DEFAULT_PLAYLIST_FILENAME);
        Log.d(TAG, "onCreate: default playlist: " + playlistFile.getAbsolutePath());
        if (!loadPlaylist(playlistFile.getAbsolutePath())) {
            Log.d(TAG, "onCreate: loading default playlist is failed");
        }
        if (playlist == null) {
            Log.d(TAG, "onCreate: playlist is null");
            playlist = new Playlist(this);
        }
        player.setPlaylist(playlist);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
        File playlistFile = new File(getFilesDir(), DEFAULT_PLAYLIST_FILENAME);
        if (!savePlaylist(playlistFile.getAbsolutePath())) {
            Log.d(TAG, "onDestroy: saving default playlist is failed");
        }
        player.free();
        BASS.BASS_Free();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
        return new PlayerBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: called");
        return super.onUnbind(intent);
    }

    //Own methods
    public PlayerState getPlayerState() {
        return player.getState();
    }

    public void play() {
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        player.stop();
    }

    public double getPosition() {
        return player.getPosition();
    }

    public void setPosition(double position) {
        player.setPosition(position);
    }

    public int getTrackLengthSeconds() {
        return player.getTrackLengthSeconds();
    }

    public void nextTrack() {
        player.nextTrack();
    }

    public void previousTrack() {
        player.previousTrack();
    }

    public void playTrack(int index) {
        boolean result = player.playTrack(index);
        Log.d(TAG, "playTrack: track " + index +
                (result ? " started playing" : " can't start playing"));
    }

    public TrackMetaData getMetadata() {
        return player.getMetadata();
    }

    public double getRemoteLoadedPercent() {
        return player.getRemoteLoadedPercent();
    }

    public boolean isTrackRemote() {
        return player.isPlayingRemote();
    }

    public boolean isTrackCached() {
        return false; // TODO: 17.11.2016 Implement track cached flag
    }

    public boolean loadPlaylist(String path) {
        if (path == null) {
            return false;
        }
        if (!path.endsWith(".vkdpls")) {
            path = path.concat(".vkdpls");
        }
        File playlistFile = new File(path);
        if (!playlistFile.exists()) {
            Log.d(TAG, "loadPlaylist: File not exist!");
            return false;
        }
        String playlistString;
        try {
            InputStream playlistFileInputStream = new FileInputStream(path);
            Reader playlistReader = new InputStreamReader(playlistFileInputStream);
            char[] buffer = new char[512];
            int charsRead = 0;
            StringBuilder builder = new StringBuilder();
            while ((charsRead = playlistReader.read(buffer)) != -1) {
                builder.append(buffer, 0, charsRead);
            }
            playlistString = builder.toString();
            playlistReader.close();
            playlistFileInputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadPlaylist: FileNotFound exception!");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "loadPlaylist: Playlist reading exception!");
            e.printStackTrace();
            return false;
        }
        Playlist loadedPlaylist = new Playlist(this);
        if (!loadedPlaylist.deserialize(playlistString)) {
            return false;
        }
        playlist = loadedPlaylist;
        return true;
    }

    public boolean savePlaylist(String path) {
        if (path == null) {
            return false;
        }
        if (!path.endsWith(".vkdpls")) {
            path = path.concat(".vkdpls");
        }
        String serializedPlaylist = playlist.serialize();
        try {
            OutputStream playlistOutputStream = new FileOutputStream(path);
            Writer playlistWriter = new OutputStreamWriter(playlistOutputStream);
            playlistWriter.write(serializedPlaylist);
            playlistWriter.close();
            playlistOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "savePlaylist: FileNotFound exception!");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "savePlaylist: Saving failed! IO exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    //Callbacks
    @Override
    public void onPlayerStateChanged(PlayerState state) {
        Log.d(TAG, "onPlayerStateChanged: i'm called :O");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_ACTION_EVENT);
        broadcastIntent.putExtra(EXTRA_EVENT, EVENT_STATE_CHANGED);
        broadcastIntent.putExtra(EXTRA_STATE, state.name());
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onPlaylistChanged() {
        Log.d(TAG, "onPlaylistChanged: i'm called;)");
        sendEventBroadcast(EVENT_PLAYLIST_CHANGED);
    }

    @Override
    public void onTrackSwitch() {
        Log.d(TAG, "onTrackSwitch: i'm called :P");
        sendEventBroadcast(EVENT_TRACK_SWITCHED);
    }

    private void sendEventBroadcast(String event) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_ACTION_EVENT);
        broadcastIntent.putExtra(EXTRA_EVENT, event);
        sendBroadcast(broadcastIntent);
    }

    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
