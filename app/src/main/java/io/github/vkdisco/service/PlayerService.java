package io.github.vkdisco.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.un4seen.bass.BASS;

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
    private Player player;
    private Playlist playlist;

    //Lifecycle methods
    @Override
    public void onCreate() {
        super.onCreate();
        BASS.BASS_Init(-1, 44100, 0);
        player = new Player();
        // TODO: 17.11.2016 Load default playlist (w loadPlaylist())
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: 17.11.2016 Save default playlist (w savePlaylist())
        player.free();
        BASS.BASS_Free();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
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
        player.playTrack(index);
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
        return false; // TODO: 17.11.2016 Implement playlist loading
    }

    public boolean savePlaylist(String path) {
        return false; // TODO: 17.11.2016 Implement playlist saving
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    //Callbacks
    @Override
    public void onPlayerStateChanged(PlayerState state) {
        //...
    }

    @Override
    public void onPlaylistChanged() {
        //...
    }

    @Override
    public void onTrackSwitch() {
        //...
    }

    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
