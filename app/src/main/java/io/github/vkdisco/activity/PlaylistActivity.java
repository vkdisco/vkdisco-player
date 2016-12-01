package io.github.vkdisco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.PlaylistAdapter;
import io.github.vkdisco.filebrowser.OpenFileActivity;
import io.github.vkdisco.model.FileTrack;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.player.Playlist;
import io.github.vkdisco.service.PlayerService;

/**
 * Playlist activity
 */

public class PlaylistActivity extends PlayerCompatActivity implements View.OnClickListener, PlaylistAdapter.OnPlaylistItemClickListener {
    private static final String TAG = "PlaylistActivity";
    private static final int REQ_CODE_ADD_FILE = 1000;

    // Playlist's view
    private RecyclerView mRVPlaylist;
    private PlaylistAdapter mPlaylistAdapter;

    private ProgressBar mPBMusicProgress;
    private TextView mTVArtist;
    private TextView mTVTitle;

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
        if (mRVPlaylist != null) {
            mRVPlaylist.setLayoutManager(new LinearLayoutManager(this));
        }

        mPBMusicProgress = ((ProgressBar) findViewById(R.id.pbMusicProgress));

        mTVArtist = ((TextView) findViewById(R.id.tvArtist));
        mTVTitle = ((TextView) findViewById(R.id.tvTitle));
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
    public void onPlaylistChanged() {
        super.onPlaylistChanged();
        PlayerService service = getPlayerService();
        if (service == null) {
            Log.d(TAG, "onPlaylistChanged: service is null :(");
            return;
        }
        Playlist playlist = service.getPlaylist();
        if (playlist == null) {
            Log.d(TAG, "onPlaylistChanged: playlist is null :(");
            return;
        }
        if (mPlaylistAdapter == null) {
            Log.d(TAG, "onPlaylistChanged: creating new adapter");
            mPlaylistAdapter = new PlaylistAdapter(playlist);
            mRVPlaylist.setAdapter(mPlaylistAdapter);
            mPlaylistAdapter.setListener(this);
        }
        Log.d(TAG, "onPlaylistChanged: data set changed");
        mPlaylistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlaylistItemClick(View view, int position) {
        PlayerService service = getPlayerService();
        if (service == null) {
            return;
        }
        service.playTrack(position);
    }

    @Override
    public void onTrackPositionUpdate(double position) {
        super.onTrackPositionUpdate(position);
        mPBMusicProgress.setProgress((int) (100 * position));
    }

    @Override
    public void onTrackSwitched() {
        super.onTrackSwitched();
        Log.d(TAG, "onTrackSwitched: called :3");
        PlayerService service = getPlayerService();
        if (service == null) {
            Log.d(TAG, "onTrackSwitched: service is null :(");
            return;
        }
        TrackMetaData metaData = service.getMetadata();
        if (metaData == null) {
            mTVArtist.setText(R.string.text_label_no_metadata);
            mTVTitle.setText(R.string.text_label_no_metadata);
            return;
        }
        mTVArtist.setText(metaData.getArtist());
        mTVTitle.setText(metaData.getTitle());
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
