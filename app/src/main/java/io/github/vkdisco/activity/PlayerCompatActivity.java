package io.github.vkdisco.activity;

import android.content.BroadcastReceiver;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;

import io.github.vkdisco.service.PlayerService;

/**
 * PlayerCompatActivity
 * Base class for all activities of this app
 * Provides connectivity with PlayerService
 */

public class PlayerCompatActivity extends AppCompatActivity {
    private BroadcastReceiver mPlayerBroadcastReceiver;
    private ServiceConnection mPlayerServiceConnection;
    private PlayerService mPlayerService;


}
