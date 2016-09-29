package io.github.vkdisco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.un4seen.bass.BASS;

import io.github.vkdisco.filebrowser.OpenFileActivity;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_LOAD_FILE = 1;

    private int mChannelHandle = 0;

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
    }

    @Override
    protected void onDestroy() {
        BASS.BASS_Free();
        super.onDestroy();
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

    private void onLoadFile() {
        Intent intent = new Intent(this, OpenFileActivity.class);
        startActivityForResult(intent, RC_LOAD_FILE);
    }

    private void onTrackPlay() {
        if (mChannelHandle == 0) {
            return;
        }
        BASS.BASS_ChannelPlay(mChannelHandle, false);
    }

    private void onTrackPause() {
        if (mChannelHandle == 0) {
            return;
        }
        BASS.BASS_ChannelPause(mChannelHandle);
    }

    private void onTrackStop() {
        if (mChannelHandle == 0) {
            return;
        }
        BASS.BASS_ChannelStop(mChannelHandle);
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
}
