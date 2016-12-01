package io.github.vkdisco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.PlaylistAdapter;
import io.github.vkdisco.filebrowser.OpenFileActivity;
import io.github.vkdisco.model.FileTrack;
import io.github.vkdisco.player.Playlist;
import io.github.vkdisco.service.PlayerService;

/**
 * Playlist activity
 */

public class PlaylistActivity extends PlayerCompatActivity implements View.OnClickListener {
    private static final String TAG = "PlaylistActivity";
    private static final int REQ_CODE_ADD_FILE = 1000;

    // Playlist's view
    private RecyclerView mRVPlaylist;
    private PlaylistAdapter mPlaylistAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        // Toolbar
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);

        // FAB
        FloatingActionButton btnAdd = ((FloatingActionButton) findViewById(R.id.btnAdd));
        if (btnAdd != null) {
            btnAdd.setOnClickListener(this);
        }

        // Playlist's recycler view
        mRVPlaylist = ((RecyclerView) findViewById(R.id.rvPlaylist));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                btnAddOnClick();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQ_CODE_ADD_FILE) && (resultCode == RESULT_OK)) {
            if (data == null) {
                return;
            }
            if (!data.hasExtra(OpenFileActivity.EXTRA_FILENAME)) {
                return;
            }
            String filename = data.getStringExtra(OpenFileActivity.EXTRA_FILENAME);
            performAddingFile(filename);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPlaylistChanged() {
        super.onPlaylistChanged();
        PlayerService service = getPlayerService();
        if (service == null) {
            return;
        }
        Playlist playlist = service.getPlaylist();
        if (playlist == null) {
            return;
        }
        if (mPlaylistAdapter == null) {
            mPlaylistAdapter = new PlaylistAdapter(playlist);
            mRVPlaylist.setAdapter(mPlaylistAdapter);
        }
        mPlaylistAdapter.notifyDataSetChanged();
    }

    private void performAddingFile(String filename) {
        if (filename == null) {
            return;
        }
        PlayerService service = getPlayerService();
        if (service == null) {
            Log.d(TAG, "btnAddOnClick: Service is null!");
            return;
        }
        Playlist playlist = service.getPlaylist();
        if (playlist == null) {
            Log.d(TAG, "btnAddOnClick: Playlist is null!");
            return;
        }
        Log.d(TAG, "performAddingFile: Opening file: " + filename);
        playlist.addTrack(new FileTrack(filename));
    }

    private void btnAddOnClick() {
        Intent openFileActivityIntent = new Intent(this, OpenFileActivity.class);
        startActivityForResult(openFileActivityIntent, REQ_CODE_ADD_FILE);
    }
}
