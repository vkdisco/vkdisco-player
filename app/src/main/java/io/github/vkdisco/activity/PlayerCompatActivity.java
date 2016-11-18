package io.github.vkdisco.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.github.vkdisco.player.Player;
import io.github.vkdisco.player.PlayerState;
import io.github.vkdisco.service.PlayerService;

/**
 * PlayerCompatActivity
 * Base class for all activities of this app
 * Provides connectivity with PlayerService
 */

public class PlayerCompatActivity extends AppCompatActivity {
    private ServiceConnection mPlayerServiceConnection;
    private BroadcastReceiver mPlayerBroadcastReceiver;
    private PlayerService mPlayerService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PlayerService wakeup
        Intent playerServiceWakeupIntent = new Intent(this, PlayerService.class);
        playerServiceWakeupIntent.putExtra(PlayerService.EXTRA_WAKEUP, true);
        startService(playerServiceWakeupIntent);
        //PlayerService binding
        mPlayerServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlayerService = ((PlayerService.PlayerBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlayerService = null;
            }
        };
        bindService(new Intent(this, PlayerService.class), mPlayerServiceConnection, 0);
        //PlayerService's broadcasts receiver (PlayerService.EVENT)
        mPlayerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(PlayerService.EXTRA_EVENT)) {
                    switch (intent.getStringExtra(PlayerService.EXTRA_EVENT)) {
                        case PlayerService.EVENT_TRACK_SWITCHED:
                            onTrackSwitched();
                            break;
                        case PlayerService.EVENT_STATE_CHANGED:
                            if (intent.hasExtra(PlayerService.EXTRA_STATE)) {
                                String stringPlayerState = intent
                                        .getStringExtra(PlayerService.EXTRA_STATE);
                                onStateChanged(PlayerState.valueOf(stringPlayerState));
                            }
                            break;
                        case PlayerService.EVENT_PLAYLIST_CHANGED:
                            onPlaylistChanged();
                            break;
                    }
                }
            }
        };
        IntentFilter playerIntentFilter = new IntentFilter(PlayerService.BROADCAST_ACTION_EVENT);
        registerReceiver(mPlayerBroadcastReceiver, playerIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PlayerService.EVENT
        unregisterReceiver(mPlayerBroadcastReceiver);
        //Unbinding
        unbindService(mPlayerServiceConnection);
    }

    public void onTrackSwitched() {
    }

    public void onStateChanged(PlayerState state) {
    }

    public void onPlaylistChanged() {
    }
}
