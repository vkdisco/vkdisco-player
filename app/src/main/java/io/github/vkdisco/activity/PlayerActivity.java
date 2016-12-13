package io.github.vkdisco.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import io.github.vkdisco.R;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.player.PlayerState;
import io.github.vkdisco.service.PlayerService;

/**
 * Player activity
 */

public class PlayerActivity extends PlayerCompatActivity implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PlayerActivity";
    private static final int MAX_PROGRESS = 10000;
    private ImageView mIVAlbumArt;
    private TextView mTVAlbum;
    private TextView mTVTitle;
    private TextView mTVArtist;
    private SeekBar mSBTrackProgress;
    private TextView mTVFullTime;
    private TextView mTVCurrentTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Toolbar
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);

        // Getting buttons
        ImageButton btnPlayPause = ((ImageButton) findViewById(R.id.btnPlayPause));
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(this);
        }
        ImageButton btnPrevious = ((ImageButton) findViewById(R.id.btnPrevious));
        if (btnPrevious != null) {
            btnPrevious.setOnClickListener(this);
        }
        ImageButton btnNext = ((ImageButton) findViewById(R.id.btnNext));
        if (btnNext != null) {
            btnNext.setOnClickListener(this);
        }
        ImageButton btnShuffle = ((ImageButton) findViewById(R.id.btnShuffle));
        if (btnShuffle != null) {
            btnShuffle.setOnClickListener(this);
        }
        ImageButton btnRepeat = ((ImageButton) findViewById(R.id.btnRepeat));
        if (btnRepeat != null) {
            btnRepeat.setOnClickListener(this);
        }

        // Getting views
        mIVAlbumArt = ((ImageView) findViewById(R.id.ivAlbumArt));
        mTVAlbum = ((TextView) findViewById(R.id.tvAlbum));
        mTVTitle = ((TextView) findViewById(R.id.tvTitle));
        mTVArtist = ((TextView) findViewById(R.id.tvArtist));
        mSBTrackProgress = ((SeekBar) findViewById(R.id.sbTrackProgress));
        if (mSBTrackProgress != null) {
            mSBTrackProgress.setMax(MAX_PROGRESS);
        }
        mTVCurrentTime = ((TextView) findViewById(R.id.tvCurrentTime));
        mTVFullTime = ((TextView) findViewById(R.id.tvFullTime));
    }

    @Override
    public void onServiceBound(PlayerService playerService) {
        super.onServiceBound(playerService);
        updateTrackMetadata();
    }

    @Override
    public void onTrackPositionUpdate(double position) {
        super.onTrackPositionUpdate(position);
        mSBTrackProgress.setProgress((int) (position * MAX_PROGRESS));
        long positionS = (long) (getPlayerService().getTrackLengthSeconds() * position);
        String positionString = String.format(Locale.getDefault(), "%02d:%02d",
                positionS / 60, positionS % 60);
        mTVCurrentTime.setText(positionString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlayPause:
                onPlayPauseClick();
                break;
            case R.id.btnPrevious:
                onPreviousClick();
                break;
            case R.id.btnNext:
                onNextClick();
                break;
            case R.id.btnShuffle:
                onShuffleClick();
                break;
            case R.id.btnRepeat:
                onRepeatClick();
                break;
        }
    }

    @Override
    public void onTrackSwitched() {
        super.onTrackSwitched();
        updateTrackMetadata();
    }

    private void onPlayPauseClick() {
        PlayerService service = getPlayerService();
        if (service.getPlayerState() == PlayerState.PLAYING) {
            service.pause();
        } else {
            service.play();
        }
        updateTrackMetadata();
    }

    private void onPreviousClick() {
        PlayerService service = getPlayerService();
        service.previousTrack();
    }

    private void onNextClick() {
        PlayerService service = getPlayerService();
        service.nextTrack();
    }

    private void onShuffleClick() {
        // TODO: 13.12.2016 Implement shuffle mode
    }

    private void onRepeatClick() {
        // TODO: 13.12.2016 Implement repeat mode
    }

    private void updateTrackMetadata() {
        PlayerService service = getPlayerService();
        if (service == null) {
            Log.d(TAG, "updateTrackMetadata: PlayerService is null!");
            return;
        }
        TrackMetaData metaData = service.getMetadata();

        if (metaData == null) {
            return;
        }

        if (metaData.getAlbumArt() == null) {
            mIVAlbumArt.setImageResource(R.drawable.ic_queue_music);
        } else {
            mIVAlbumArt.setImageBitmap(metaData.getAlbumArt());
        }
        mTVTitle.setText(metaData.getTitle());
        mTVArtist.setText(metaData.getArtist());
        mTVAlbum.setText(metaData.getAlbum());
        long durationMs = metaData.getDuration();
        mTVFullTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                getMinutes(durationMs), getSeconds(durationMs)));
    }

    private int getSeconds(long ms) {
        ms /= 1000;
        return (int) (ms % 60);
    }

    private int getMinutes(long ms) {
        ms /= 1000;
        return (int) (ms / 60);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
