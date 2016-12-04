package io.github.vkdisco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.PlaylistAdapter;
import io.github.vkdisco.filebrowser.OpenFileActivity;
import io.github.vkdisco.model.FileTrack;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.player.PlayerState;
import io.github.vkdisco.player.Playlist;
import io.github.vkdisco.service.PlayerService;

/**
 * Playlist activity
 */

public class PlaylistActivity extends PlayerCompatActivity implements View.OnClickListener, PlaylistAdapter.OnPlaylistItemClickListener {
    private static final String TAG = "PlaylistActivity";
    private static final int REQ_CODE_ADD_FILE = 1000;

    // Custom scope for our app
    private static final String[] sScope = new String[]{
            VKScope.FRIENDS,
            VKScope.AUDIO,
            VKScope.NOHTTPS,
    };

    // Playlist's view
    private RecyclerView mRVPlaylist;
    private PlaylistAdapter mPlaylistAdapter;

    private ProgressBar mPBMusicProgress;
    private TextView mTVArtist;
    private TextView mTVTitle;

    private ImageView mIVAlbumArt;

    ImageButton mBtnPlayPause;

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

        mIVAlbumArt = ((ImageView) findViewById(R.id.ivAlbumArt));

        mBtnPlayPause = ((ImageButton) findViewById(R.id.btnPlayPause));
        if (mBtnPlayPause != null) {
            mBtnPlayPause.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnVK) {
            if (VKSdk.isLoggedIn()) {
                VKSdk.login(this, sScope);
            } else {
                VKSdk.logout();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                btnAddOnClick();
                break;
            case R.id.btnPlayPause:
                btnPlayPauseClick();
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

        Bitmap albumArt = metaData.getAlbumArt();
        if (albumArt == null) {
            mIVAlbumArt.setImageResource(R.mipmap.ic_music_white);
        } else {
            mIVAlbumArt.setImageBitmap(albumArt);
        }
    }

    //// FIXME: 04.12.16 Drawable resource doesn't exist
    @Override
    public void onStateChanged(PlayerState state) {
        super.onStateChanged(state);
        Log.d(TAG, "onStateChanged: my new state: " + state.name());
        switch (state) {
            case PLAYING:
                mBtnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                break;
            case PAUSED:
                mBtnPlayPause.setImageResource(android.R.drawable.ic_media_play);
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

    private void btnPlayPauseClick() {
        PlayerService service = getPlayerService();
        if (service == null) {
            return;
        }
        PlayerState state = service.getPlayerState();
        if (state == PlayerState.PLAYING) {
            service.pause();
            return;
        }
        if (state == PlayerState.PAUSED) {
            service.play();
        }
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
