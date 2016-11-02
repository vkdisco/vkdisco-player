package io.github.vkdisco.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class PlayerService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("COMMON", this.getClass().getName() + ".onCreate()");
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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("COMMON", this.getClass().getName() + ".onBind(" + ((intent == null) ? "null" : "obj") + ")");
        return null;
    }
}
