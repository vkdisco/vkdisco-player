package io.github.vkdisco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.un4seen.bass.BASS;

import io.github.vkdisco.filebrowser.OpenFileActivity;

public class SampleActivity extends AppCompatActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int RC_LOAD_FILE = 1;

    private int mChannelHandle = 0;
    private TrackProgressUpdateThread mUpdateThread = null;
    private boolean mPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initBass();
        Button btnLoadFile = ((Button) findViewById(R.id.btnLoadFile));
        if (btnLoadFile != null) {
            btnLoadFile.setOnClickListener(this);
        }
        Button btnPlay = ((Button) findViewById(R.id.btnPlay));
        if (btnPlay != null) {
            btnPlay.setOnClickListener(this);
        }
        Button btnPause = ((Button) findViewById(R.id.btnPause));
        if (btnPause != null) {
            btnPause.setOnClickListener(this);
        }
        Button btnStop = ((Button) findViewById(R.id.btnStop));
        if (btnStop != null) {
            btnStop.setOnClickListener(this);
        }
        SeekBar sbTrackProgress = ((SeekBar) findViewById(R.id.sbTrackProgress));
        if (sbTrackProgress != null) {
            sbTrackProgress.setOnSeekBarChangeListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        stopProgressUpdate();
        BASS.BASS_Free();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_LOAD_FILE:
                    if (data != null) {
                        String filename = data.getStringExtra(OpenFileActivity.EXTRA_FILENAME);
                        loadFileByName(filename);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadFile:
                onLoadFile();
                break;
            case R.id.btnPlay:
                onTrackPlay();
                break;
            case R.id.btnPause:
                onTrackPause();
                break;
            case R.id.btnStop:
                onTrackStop();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        long total = BASS.BASS_ChannelGetLength(mChannelHandle, BASS.BASS_POS_BYTE);
        long position = Math.round((1.0 * progress / 100) * total);
        BASS.BASS_ChannelSetPosition(mChannelHandle, position, BASS.BASS_POS_BYTE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mPlaying) {
            stopProgressUpdate();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mPlaying) {
            startProgressUpdate();
        }
    }

    private void onLoadFile() {
        Intent intent = new Intent(this, OpenFileActivity.class);
        startActivityForResult(intent, RC_LOAD_FILE);
    }

    private void onTrackPlay() {
        if (mChannelHandle == 0) {
            return;
        }
        BASS.BASS_ChannelPlay(mChannelHandle, false);
        startProgressUpdate();
        mPlaying = true;
    }

    private void onTrackPause() {
        if (mChannelHandle == 0) {
            return;
        }
        stopProgressUpdate();
        BASS.BASS_ChannelPause(mChannelHandle);
        mPlaying = false;
    }

    private void onTrackStop() {
        if (mChannelHandle == 0) {
            return;
        }
        stopProgressUpdate();
        mPlaying = false;
        BASS.BASS_ChannelStop(mChannelHandle);
        BASS.BASS_ChannelSetPosition(mChannelHandle, 0, BASS.BASS_POS_BYTE);
    }

    private void initBass() {
        if (!BASS.BASS_Init(-1, 44100, 0)) {
            Button btnLoadFile = ((Button) findViewById(R.id.btnLoadFile));
            if (btnLoadFile != null) {
                btnLoadFile.setEnabled(false);
            }
            Toast.makeText(this, R.string.text_bass_device_init_fail, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void loadFileByName(String filename) {
        if (filename == null) {
            return;
        }
        mChannelHandle = BASS.BASS_StreamCreateFile(filename, 0, 0, 0);
        if (mChannelHandle == 0) {
            Toast.makeText(this, R.string.text_bass_streamcreatefile_fail, Toast.LENGTH_SHORT)
                    .show();
            playControlsEnabled(false);
            return;
        }
        playControlsEnabled(true);
    }

    private void startProgressUpdate() {
        if (mUpdateThread != null) {
            return;
        }
        mUpdateThread = new TrackProgressUpdateThread();
        mUpdateThread.setActivity(this);
        SeekBar sbTrackProgress = ((SeekBar) findViewById(R.id.sbTrackProgress));
        mUpdateThread.setSeekBar(sbTrackProgress);
        mUpdateThread.setChannelHandle(mChannelHandle);
        mUpdateThread.start();
    }

    private void stopProgressUpdate() {
        if (mUpdateThread == null) {
            return;
        }
        boolean tryJoinThread = true;
        mUpdateThread.setRunning(false);
        while (tryJoinThread) {
            try {
                mUpdateThread.join();
                tryJoinThread = false;
            } catch (InterruptedException ignored) {
            }
        }
        mUpdateThread = null;
    }

    private void playControlsEnabled(boolean enabled) {
        Button btnPlay = ((Button) findViewById(R.id.btnPlay));
        if (btnPlay != null) {
            btnPlay.setEnabled(enabled);
        }
        Button btnPause = ((Button) findViewById(R.id.btnPause));
        if (btnPause != null) {
            btnPause.setEnabled(enabled);
        }
        Button btnStop = ((Button) findViewById(R.id.btnStop));
        if (btnStop != null) {
            btnStop.setEnabled(enabled);
        }
    }

    public static class TrackProgressUpdateThread extends Thread {
        private int mChannelHandle = 0;
        private SeekBar mSBTrackProgress = null;
        private Activity mActivity = null;
        private boolean mRunning = true;

        @Override
        public void run() {
            while (mRunning) {
                if (mChannelHandle != 0) {
                    long total = BASS.BASS_ChannelGetLength(mChannelHandle, BASS.BASS_POS_BYTE);
                    long position = BASS.BASS_ChannelGetPosition(mChannelHandle, BASS.BASS_POS_BYTE);
                    if (position >= total) {
                        mRunning = false;
                    }
                    final int sbProgress = ((int) Math.round((1.0 * position / total) * 100));
                    if (mActivity != null && mSBTrackProgress != null) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSBTrackProgress.setProgress(sbProgress);
                            }
                        });
                    }
                }
            }
        }

        public void setRunning(boolean run) {
            this.mRunning = run;
        }

        public void setChannelHandle(int channelHandle) {
            this.mChannelHandle = channelHandle;
        }

        public void setActivity(Activity activity) {
            this.mActivity = activity;
        }

        public void setSeekBar(SeekBar seekBar) {
            this.mSBTrackProgress = seekBar;
        }
    }
}
