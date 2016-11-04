package io.github.vkdisco.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.github.vkdisco.service.PlayerService;

public class PlayerCompatActivity extends AppCompatActivity {
    private PlayerService mPlayerService;
    private ServiceConnection mPlayerServiceConnection;
    private BroadcastReceiver mPlayerBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("COMMON", this.getClass().getName() + ".onCreate(" + ((savedInstanceState == null) ? "null" : "obj") + ")");
        startService(
                new Intent(getApplicationContext(), PlayerService.class)
                        .putExtra(PlayerService.INTENT_ACTION, PlayerService.ACTION_WAKEUP)
        );
        mPlayerServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service != null) {
                    mPlayerService = ((PlayerService.PlayerBinder) service).getService();
                }
                Log.d("COMMON", this.getClass().getName() + ".onServiceConnected(" + ((service == null) ? "null" : "obj") + ")");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlayerService = null;
                Log.d("COMMON", this.getClass().getName() + ".onServiceDisconnected()");
            }
        };
        mPlayerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.hasExtra(PlayerService.INTENT_EVENT)) {
                    return;
                }
                switch (intent.getStringExtra(PlayerService.INTENT_EVENT)) {
                    case PlayerService.EVENT_TRACK_SWITCHED:
                        onTrackSwitched();
                        break;
                    case PlayerService.EVENT_PLAYER_STATE_CHANGED:
                        onPlayerStateChanged();
                        break;
                    case PlayerService.EVENT_PLAYLIST_CHANGED:
                        onPlaylistChanged();
                        break;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("COMMON", this.getClass().getName() + ".onStart()");
        bindService(new Intent(getApplicationContext(), PlayerService.class), mPlayerServiceConnection, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("COMMON", this.getClass().getName() + ".onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("COMMON", this.getClass().getName() + ".onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("COMMON", this.getClass().getName() + ".onStop()");
        unbindService(mPlayerServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("COMMON", this.getClass().getName() + ".onDestroy()");
    }

    protected PlayerService getPlayerService() {
        return mPlayerService;
    }

    protected void onTrackSwitched() {
    }

    protected void onPlaylistChanged() {
    }

    protected void onPlayerStateChanged() {
    }
}
