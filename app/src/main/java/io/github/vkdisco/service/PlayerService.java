package io.github.vkdisco.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.github.vkdisco.player.PlayerState;
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
    // TODO: 16.11.2016 Add Player field
    // TODO: 16.11.2016 Add Playlist field

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
}
