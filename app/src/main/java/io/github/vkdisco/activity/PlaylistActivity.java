package io.github.vkdisco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiUser;

import java.io.File;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.PlaylistAdapter;
import io.github.vkdisco.adapter.PlaylistAdapter.OnPlaylistItemClickListener;
import io.github.vkdisco.fragment.AboutDialog;
import io.github.vkdisco.fragment.FileDialog;
import io.github.vkdisco.fragment.VKFriendsDialog;
import io.github.vkdisco.fragment.VKTracksDialog;
import io.github.vkdisco.fragment.interfaces.OnTrackSelectedListener;
import io.github.vkdisco.fragment.interfaces.OnUserSelectedListener;
import io.github.vkdisco.model.FileTrack;
import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.player.Player;
import io.github.vkdisco.player.PlayerState;
import io.github.vkdisco.player.Playlist;
import io.github.vkdisco.service.PlayerService;

/**
 * Playlist activity
 */

public class PlaylistActivity extends PlayerCompatActivity
        implements OnClickListener, OnPlaylistItemClickListener, OnUserSelectedListener,
        OnTrackSelectedListener, FileDialog.OnFileSelectedListener {
    private static final String TAG = "PlaylistActivity";

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

    private boolean mPlaylistSwapMode = false;
    private int mTrackIndexToSwap = -1;

    ImageButton mBtnPlayPause;
    private boolean fabStatus = false;
    private FloatingActionButton btnAdd;

    private FloatingActionButton btnFile;
    private FloatingActionButton btnVK;
    private Animation show_fab_file;
    private Animation hide_fab_file;
    private Animation show_fab_vk;
    private Animation hide_fab_vk;

    private boolean mExportMode = false;
    private boolean mImportMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        // Toolbar
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);

        // Player bar
        LinearLayout llPlayerBar = ((LinearLayout) findViewById(R.id.llPlayerBar));
        if (llPlayerBar != null) {
            llPlayerBar.setOnClickListener(this);
        }

        // FABs
        btnAdd = ((FloatingActionButton) findViewById(R.id.btnAdd));
        if (btnAdd != null) {
            btnAdd.setOnClickListener(this);
        }
        btnFile = (FloatingActionButton) findViewById(R.id.btnFile);
        if (btnFile != null) {
            btnFile.setOnClickListener(this);
        }
        btnVK = (FloatingActionButton) findViewById(R.id.btnVK);
        if (btnVK != null) {
            btnVK.setOnClickListener(this);
        }

        // Animations
        show_fab_file = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_file);
        hide_fab_file = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_file);
        show_fab_vk = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_vk);
        hide_fab_vk = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_vk);

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
    protected void onResume() {
        super.onResume();
        onPlaylistChanged();
        onTrackSwitched();
    }

    @Override
    public void onServiceBound(PlayerService playerService) {
        super.onServiceBound(playerService);
        onPlaylistChanged();
        onTrackSwitched();
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
            if (!VKSdk.isLoggedIn()) {
                VKSdk.login(this, sScope);
            } else {
                VKSdk.logout();
            }
            return true;
        }
        if (id == R.id.menuExportPlaylist) {
            FileDialog fileDialog = new FileDialog();
            fileDialog.setSelectMode(FileDialog.SelectMode.SAVE_FILE);
            fileDialog.setStartFile(new File("/mnt/"));
            fileDialog.setListener(this);
            mExportMode = true;
            fileDialog.show(getSupportFragmentManager(), "PlaylistActivity");
        }
        if (id == R.id.menuImportPlaylist) {
            FileDialog fileDialog = new FileDialog();
            fileDialog.setSelectMode(FileDialog.SelectMode.SINGLE_FILE);
            fileDialog.setListener(this);
            fileDialog.setStartFile(new File("/mnt/"));
            mImportMode = true;
            fileDialog.show(getSupportFragmentManager(), "PlaylistActivity");
        }
        if (id == R.id.menuClearPlaylist) {
            PlayerService service = getPlayerService();
            if (service == null) {
                return true;
            }
            Playlist playlist = service.getPlaylist();
            if (playlist == null) {
                return true;
            }
            playlist.clear();
        }
        if (id == R.id.menuAbout) {
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.show(getSupportFragmentManager(), "AboutDialog");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPlayerBar:
                playerBarClick();
                break;
            case R.id.btnAdd:
                if (!fabStatus) {
                    expandFAB();
                    fabStatus = true;
                } else {
                    hideFAB();
                    fabStatus = false;
                }
                break;
            case R.id.btnFile:
                btnAddFromFileOnClick();
                break;
            case R.id.btnVK:
                btnAddFromVKOnClick();
                break;
            case R.id.btnPlayPause:
                btnPlayPauseClick();
                break;
        }
    }

    private void playerBarClick() {
        Intent playerActivityIntent = new Intent(this, PlayerActivity.class);
        startActivity(playerActivityIntent);
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
    public void onPlaylistItemClick(View view, final int position) {
        if ((view.getId() == R.id.imgBtnMore) && (!mPlaylistSwapMode)) {

            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenuInflater()
                    .inflate(R.menu.menu_three_dots, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.swap) {
                        startSwappingTrack(position);
                        return true;
                    }

                    if (id == R.id.delete) {
                        performDeleteTrack(position);
                        return true;
                    }

                    return false;
                }

            });

            popup.show();
            return;
        }

        PlayerService service = getPlayerService();
        if (service == null) {
            return;
        }
        if (mPlaylistSwapMode) {
            finishSwappingTrack(position);
        } else {
            service.playTrack(position);
        }
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
            Log.d(TAG, "btnAddFromFileOnClick: Service is null!");
            return;
        }
        Playlist playlist = service.getPlaylist();
        if (playlist == null) {
            Log.d(TAG, "btnAddFromFileOnClick: Playlist is null!");
            return;
        }
        Log.d(TAG, "performAddingFile: Opening file: " + filename);
        playlist.addTrack(new FileTrack(filename));
    }

    private void btnAddFromFileOnClick() {
        FileDialog fileDialog = new FileDialog();
        fileDialog.setListener(this);
        fileDialog.setSelectMode(FileDialog.SelectMode.MULTIPLE_FILE);
        fileDialog.setStartFile(new File("/mnt/"));
        fileDialog.show(getSupportFragmentManager(), "FileDialog");
    }

    private void btnAddFromVKOnClick() {
        if (!VKSdk.isLoggedIn()) {
            Toast.makeText(this, "Please, sign in VK", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        DialogFragment dialog = new VKFriendsDialog();
        dialog.show(getSupportFragmentManager(), "vk_friends_dialog");
    }

    private void expandFAB() {

        //Floating Action Button File
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnFile.getLayoutParams();
        layoutParams.rightMargin += (int) (btnFile.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (btnFile.getHeight() * 0.25);
        btnFile.setLayoutParams(layoutParams);
        btnFile.startAnimation(show_fab_file);
        btnFile.setClickable(true);

        //Floating Action Button VK
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) btnVK.getLayoutParams();
        layoutParams2.rightMargin += (int) (btnVK.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (btnVK.getHeight() * 1.5);
        btnVK.setLayoutParams(layoutParams2);
        btnVK.startAnimation(show_fab_vk);
        btnVK.setClickable(true);
    }


    private void hideFAB() {

        //Floating Action Button File
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnFile.getLayoutParams();
        layoutParams.rightMargin -= (int) (btnFile.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (btnFile.getHeight() * 0.25);
        btnFile.setLayoutParams(layoutParams);
        btnFile.startAnimation(hide_fab_file);
        btnFile.setClickable(false);

        //Floating Action Button VK
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) btnVK.getLayoutParams();
        layoutParams2.rightMargin -= (int) (btnVK.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (btnVK.getHeight() * 1.5);
        btnVK.setLayoutParams(layoutParams2);
        btnVK.startAnimation(hide_fab_vk);
        btnVK.setClickable(false);
    }

    @Override
    public void onUserSelected(VKApiUser vkApiUser) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", vkApiUser.id);
        DialogFragment dialog = new VKTracksDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "vk_track_dialog");
    }

    @Override
    public void onTrackSelected(Track track) {
        PlayerService service = getPlayerService();
        if (service == null) {
            Log.d(TAG, "onTrackSelected: Service is null!");
            return;
        }
        Playlist playlist = service.getPlaylist();
        if (playlist == null) {
            Log.d(TAG, "onTrackSelected: Playlist is null!");
            return;
        }
        Log.d(TAG, "onTrackSelected: Loading url of track: " + track.getMetaData().getTitle());
        playlist.addTrack(track);
    }

    @Override
    public void onFileSelected(File file) {
        if (file == null) {
            Log.d(TAG, "onFileSelected: Selected file is null!");
            return;
        }
        PlayerService service = getPlayerService();
        if (mImportMode) {
            if (service != null) {
                service.loadPlaylist(file.getAbsolutePath());
            }
            mImportMode = false;
            return;
        }
        if (mExportMode) {
            if (service != null) {
                service.savePlaylist(file.getAbsolutePath());
            }
            mExportMode = false;
            return;
        }
        performAddingFile(file.getAbsolutePath());
    }

    private void performDeleteTrack(int position) {
        PlayerService service = getPlayerService();
        if (service == null) {
            return;
        }
        Playlist playlist = service.getPlaylist();
        if (playlist == null) {
            return;
        }
        playlist.removeTrack(position);
    }

    private void startSwappingTrack(int position) {
        mPlaylistAdapter.setFlashedItem(position);
        mPlaylistSwapMode = true;
        mTrackIndexToSwap = position;
    }

    private void finishSwappingTrack(int position) {
        PlayerService service = getPlayerService();
        if (service == null) {
            return;
        }
        Playlist playlist = service.getPlaylist();
        if (playlist == null) {
            return;
        }
        playlist.swap(position, mTrackIndexToSwap);
        mTrackIndexToSwap = -1;
        mPlaylistSwapMode = false;
        mPlaylistAdapter.unflashItem();
    }
}
