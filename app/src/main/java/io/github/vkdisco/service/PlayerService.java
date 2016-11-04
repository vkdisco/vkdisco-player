package io.github.vkdisco.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import io.github.vkdisco.audio.Player;

public class PlayerService extends Service {

    public static final String INTENT_ACTION = "PlayerService.INTENT_ACTION";
    public static final String ACTION_WAKEUP = "WAKEUP";
    public static final String INTENT_EVENT = "PlayerService.INTENT_EVENT";
    public static final String EVENT_TRACK_SWITCHED = "TRACK_SWITCHED";
    public static final String EVENT_PLAYLIST_CHANGED = "PLAYLIST_CHANGED";
    public static final String EVENT_PLAYER_STATE_CHANGED = "PLAYER_STATE_CHANGED";
    private Player player = new Player();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("COMMON", this.getClass().getName() + ".onCreate()");
        //BASS Init here
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("COMMON", this.getClass().getName() + ".onStartCommand(" + ((intent == null) ? "null" : "obj") + ")");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("COMMON", this.getClass().getName() + ".onDestroy()");
        //BASS Close here
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("COMMON", this.getClass().getName() + ".onBind(" + ((intent == null) ? "null" : "obj") + ")");
        return null;
    }

    public class PlayerBinder extends Binder { //INTENTIONALLY NOT STATIC!
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
