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
import android.util.Log;

import io.github.vkdisco.player.PlayerState;
import io.github.vkdisco.service.PlayerService;

/**
 * PlayerCompatActivity
 * Base class for all activities of this app
 * Provides connectivity with PlayerService
 */

public class PlayerCompatActivity extends AppCompatActivity {
    private static final String TAG = "PlayerCompatActivity";
    private static final int DEFAULT_UPDATE_FREQUENCY = 4; //Hz
    private ServiceConnection mPlayerServiceConnection;
    private BroadcastReceiver mPlayerBroadcastReceiver;
    private PlayerService mPlayerService;
    private TrackPositionUpdater mPositionUpdater;
    private Thread mPositionUpdaterThread;

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
                onServiceBound(mPlayerService);
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
        //Position updater
        mPositionUpdater = new TrackPositionUpdater();
        mPositionUpdater.setListener(this);
        mPositionUpdater.setUpdateFrequency(DEFAULT_UPDATE_FREQUENCY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PlayerService.EVENT
        unregisterReceiver(mPlayerBroadcastReceiver);
        //Unbinding
        unbindService(mPlayerServiceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startPositionUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPositionUpdate();
    }

    public PlayerService getPlayerService() {
        return mPlayerService;
    }

    public void onTrackSwitched() {
    }

    public void onStateChanged(PlayerState state) {
    }

    public void onPlaylistChanged() {
    }

    public void onServiceBound(PlayerService playerService) {
    }

    public void onTrackPositionUpdate(double position) {
    }

    private void startPositionUpdate() {
        if (mPositionUpdaterThread != null) {
            return;
        }
        mPositionUpdaterThread = new Thread(mPositionUpdater);
        mPositionUpdater.setRunning(true);
        mPositionUpdaterThread.start();
    }

    private void stopPositionUpdate() {
        if (mPositionUpdaterThread == null) {
            return;
        }
        mPositionUpdater.setRunning(false);
        boolean tryAgain = true;
        while (tryAgain) {
            try {
                mPositionUpdaterThread.join();
                tryAgain = false;
            } catch (InterruptedException ignored) {
            }
        }
        mPositionUpdaterThread = null;
    }

    private void callTrackPositionUpdate() {
        if (getPlayerService() == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onTrackPositionUpdate(getPlayerService().getPosition());
            }
        });
    }

    public static class TrackPositionUpdater implements Runnable {
        private int mUpdateFrequency;
        private boolean mRunning = true;
        private PlayerCompatActivity mListener;

        @Override
        public void run() {
            while (mRunning) {
                if (mListener != null) {
                    mListener.callTrackPositionUpdate();
                }
                try {
                    Thread.sleep(1000 / mUpdateFrequency);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        public void setUpdateFrequency(int updateFrequency) { //In Hz
            this.mUpdateFrequency = updateFrequency;
        }

        public void setRunning(boolean running) {
            this.mRunning = running;
        }

        public void setListener(PlayerCompatActivity listener) {
            this.mListener = listener;
        }
    }
}
