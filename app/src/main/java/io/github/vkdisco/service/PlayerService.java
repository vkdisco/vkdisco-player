package io.github.vkdisco.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * PlayerService
 * Contains Player, provides it communication with user,
 * foreground and background work
 */

public class PlayerService extends Service {
    // TODO: 16.11.2016 Add Player field
    // TODO: 16.11.2016 Add Playlist field

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
