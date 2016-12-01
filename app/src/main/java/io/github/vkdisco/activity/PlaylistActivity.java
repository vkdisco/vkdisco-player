package io.github.vkdisco.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.vkdisco.R;

/**
 * Playlist activity
 */

public class PlaylistActivity extends PlayerCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
    }
}
