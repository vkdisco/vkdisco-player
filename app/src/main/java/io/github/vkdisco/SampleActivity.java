package io.github.vkdisco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.un4seen.bass.BASS;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import io.github.vkdisco.filebrowser.OpenFileActivity;
import io.github.vkdisco.fragments.AudioFragment;
import io.github.vkdisco.fragments.FriendDialogFragment;

public class SampleActivity extends AppCompatActivity
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        AudioFragment.OnAudioSelectedListener, FriendDialogFragment.OnFriendSelectedListener {
    private static final int RC_LOAD_FILE = 1;

    private int mChannelHandle = 0;
    private TrackProgressUpdateThread mUpdateThread = null;
    private LevelUpdateThread mLevelUpdateThread = null;
    private boolean mPlaying = false;

    private Button btnLogin;
    private Button btnLogout;
    private Button btnChooseFriend;
    private boolean isResumed = false;

    // Custom scope for our app
    private static final String[] sScope = new String[]{
            VKScope.FRIENDS,
            VKScope.AUDIO,
            VKScope.NOHTTPS,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initBass(); //BASS initialization
        initViews(); //Views initialization

        if (savedInstanceState == null) {
            showAudioFragment(0);
        }

        btnLogin.setVisibility(View.VISIBLE);

        // Checking of logging in VK
        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (isResumed) {
                    switch (res) {
                        case LoggedOut:
                            btnLogin.setVisibility(View.INVISIBLE);
                            break;
                        case LoggedIn:
                            btnLogout.setVisibility(View.VISIBLE);
                            btnChooseFriend.setVisibility(View.VISIBLE);
                            break;
                        case Pending:
                            break;
                        case Unknown:
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        stopLevelUpdate(); //Stopping all updates
        stopTrackProgressUpdate();
        BASS.BASS_Free(); //Freeing BASS
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (VKSdk.isLoggedIn()) {
            btnLogout.setVisibility(View.VISIBLE);
            btnChooseFriend.setVisibility(View.VISIBLE);
            showAudioFragment(0);
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                btnLogin.setVisibility(View.INVISIBLE);
                btnLogout.setVisibility(View.VISIBLE);
                btnChooseFriend.setVisibility(View.VISIBLE);
                showAudioFragment(0);
            }

            @Override
            public void onError(VKError error) {

            }
        };
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_LOAD_FILE: //If user chosen file to open
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
            case R.id.btnLogIn:
                VKSdk.login(this, sScope);
                break;
            case R.id.btnLogOut:
                VKSdk.logout();
                if (!VKSdk.isLoggedIn()) {
                    btnLogin.setVisibility(View.VISIBLE);
                    btnLogout.setVisibility(View.INVISIBLE);
                    btnChooseFriend.setVisibility(View.INVISIBLE);
                    showAudioFragment(0);
                }
                break;
            case R.id.btnChooseFriend:
                showFriendDialog();
                break;
        }
    }

    private void showFriendDialog() {
        DialogFragment fragment = new FriendDialogFragment();
        fragment.show(getSupportFragmentManager(), "friends_dialog");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) { //Ignoring changing progress by setProgress(int)
            return;
        }
        long total = BASS.BASS_ChannelGetLength(mChannelHandle, BASS.BASS_POS_BYTE); //Setting up position
        long position = Math.round((1.0 * progress / 100) * total);
        BASS.BASS_ChannelSetPosition(mChannelHandle, position, BASS.BASS_POS_BYTE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Stopping thread-updater if manual track position changing
        if (mPlaying) {
            stopTrackProgressUpdate();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mPlaying) {
            startTrackProgressUpdate();
        }
    }

    private void onLoadFile() {
        onTrackStop();
        if (mChannelHandle != 0) { //If we opening another track, we need to free already opened
            //(if it opened)
            BASS.BASS_StreamFree(mChannelHandle);
        }
        Intent intent = new Intent(this, OpenFileActivity.class);
        startActivityForResult(intent, RC_LOAD_FILE);
    }

    private void onTrackPlay() {
        if (mChannelHandle == 0) {
            return;
        }
        BASS.BASS_ChannelPlay(mChannelHandle, false);
        //Track playing started, so starting threads-updaters
        startLevelUpdate();
        startTrackProgressUpdate();
        mPlaying = true;
    }

    private void onTrackPause() {
        if (mChannelHandle == 0) {
            return;
        }
        //Track to be paused - stopping threads-updaters
        stopLevelUpdate();
        stopTrackProgressUpdate();
        BASS.BASS_ChannelPause(mChannelHandle);
        mPlaying = false;
    }

    private void onTrackStop() {
        if (mChannelHandle == 0) {
            return;
        }
        stopLevelUpdate();
        stopTrackProgressUpdate();
        mPlaying = false;
        BASS.BASS_ChannelStop(mChannelHandle);
        BASS.BASS_ChannelSetPosition(mChannelHandle, 0, BASS.BASS_POS_BYTE);
    }

    private void showAudioFragment(int id) {
        Fragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    private void initBass() {
        if (!BASS.BASS_Init(-1, 44100, 0)) {
            //Initialization default device (-1) at sample frequency 44100kHz without flags (0)
            Button btnLoadFile = ((Button) findViewById(R.id.btnLoadFile));
            if (btnLoadFile != null) {
                btnLoadFile.setEnabled(false);
            }
            Toast.makeText(this, R.string.text_bass_device_init_fail, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void initViews() { //Setting handlers to all views
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
        btnLogin = (Button) findViewById(R.id.btnLogIn);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(this);
        }
        btnLogout = (Button) findViewById(R.id.btnLogOut);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(this);
        }
        btnChooseFriend = (Button) findViewById(R.id.btnChooseFriend);
        if (btnChooseFriend != null) {
            btnChooseFriend.setOnClickListener(this);
        }
        SeekBar sbTrackProgress = ((SeekBar) findViewById(R.id.sbTrackProgress));
        if (sbTrackProgress != null) {
            sbTrackProgress.setOnSeekBarChangeListener(this);
        }
    }

    private void loadFileByName(String filename) {
        if (filename == null) {
            return;
        }
        mChannelHandle = BASS.BASS_StreamCreateFile(filename, 0, 0, 0); //Loading audio file by name
        if (mChannelHandle == 0) {
            Toast.makeText(this, R.string.text_bass_streamcreatefile_fail, Toast.LENGTH_SHORT)
                    .show();
            playControlsEnabled(false);
            return;
        }
        playControlsEnabled(true);
    }

    private void loadFileByURL(String url) {
        if (url == null) {
            return;
        }
        mChannelHandle = BASS.BASS_StreamCreateURL(url, 0, 0, null, 0); // Loading audio file by url
        if (mChannelHandle == 0) {
            Toast.makeText(this, "Steam from url creation fail!", Toast.LENGTH_SHORT)
                    .show();
            playControlsEnabled(false);
            return;
        }
        playControlsEnabled(true);
    }


    private void startTrackProgressUpdate() {
        if (mUpdateThread != null) {
            return;
        }
        mUpdateThread = new TrackProgressUpdateThread(); //Creating and setting up progress thread-updater
        mUpdateThread.setActivity(this);
        mUpdateThread.setChannelHandle(mChannelHandle);
        SeekBar sbTrackProgress = ((SeekBar) findViewById(R.id.sbTrackProgress));
        mUpdateThread.setSeekBar(sbTrackProgress);
        mUpdateThread.start();
    }

    private void stopTrackProgressUpdate() {
        if (mUpdateThread == null) {
            return;
        }
        boolean tryJoinThread = true;
        mUpdateThread.setRunning(false);
        while (tryJoinThread) { //Trying to join to thread-updater
            try {
                mUpdateThread.join();
                tryJoinThread = false;
            } catch (InterruptedException ignored) {
            }
        }
        mUpdateThread = null;
    }

    private void startLevelUpdate() { //All the same, but for level indicators
        if (mLevelUpdateThread != null) {
            return;
        }
        mLevelUpdateThread = new LevelUpdateThread();
        mLevelUpdateThread.setActivity(this);
        mLevelUpdateThread.setChannelHandle(mChannelHandle);
        ProgressBar pbLeftLevel = ((ProgressBar) findViewById(R.id.pbIndicationLeft));
        mLevelUpdateThread.setProgressBarLevelLeft(pbLeftLevel);
        ProgressBar pbRightLevel = ((ProgressBar) findViewById(R.id.pbIndicationRight));
        mLevelUpdateThread.setProgressBarLevelRight(pbRightLevel);
        mLevelUpdateThread.start();
    }

    private void stopLevelUpdate() {
        if (mLevelUpdateThread == null) {
            return;
        }
        boolean tryJoinThread = true;
        mLevelUpdateThread.setRunning(false);
        while (tryJoinThread) {
            try {
                mLevelUpdateThread.join();
                tryJoinThread = false;
            } catch (InterruptedException ignored) {
            }
        }
        mLevelUpdateThread = null;
    }

    private void playControlsEnabled(boolean enabled) { //Enabling or disabling player controls
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

    @Override
    public void onAudioSelected(String url) {
        loadFileByURL(url);
    }

    @Override
    public void onFriendSelected(int id) {
        showAudioFragment(id);
    }

    /**
     * Track progress need to be updated periodically.
     * If we will do this in main thread (i.e. UI thread), we'll
     * freeze UI
     */
    public static class TrackProgressUpdateThread extends Thread {
        private static final int UPDATE_DELTA_MS = 50;
        private int mChannelHandle = 0;
        private SeekBar mSBTrackProgress = null;
        private Activity mActivity = null;
        private boolean mRunning = true;

        @Override
        public void run() {
            while (mRunning) {
                if (mChannelHandle != 0) {
                    updateProgress();
                    try {
                        Thread.sleep(UPDATE_DELTA_MS);
                    } catch (InterruptedException ignored) {
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

        private void updateProgress() {
            if (mActivity == null || mSBTrackProgress == null) {
                return;
            }
            long total = BASS.BASS_ChannelGetLength(mChannelHandle, BASS.BASS_POS_BYTE);
            long position = BASS.BASS_ChannelGetPosition(mChannelHandle, BASS.BASS_POS_BYTE);
            if (position >= total) {
                mRunning = false;
            }
            final int sbProgress = ((int) Math.round((1.0 * position / total) * 100));
            mActivity.runOnUiThread(new Runnable() { //ALL UI-OPERATIONS MUST BE EXECUTED FROM UI-THREAD
                //UI-OPERATIONS FORBIDDEN IN NON-UI THREADS
                @Override
                public void run() {
                    mSBTrackProgress.setProgress(sbProgress);
                }
            });
        }
    }

    /**
     * Same thing for track left and right levels
     */
    public static class LevelUpdateThread extends Thread {
        private static final int UPDATE_DELTA_MS = 75;
        public static final int MAX_LEVEL = 32768;
        private int mChannelHandle = 0;
        private ProgressBar mPBLevelLeft = null;
        private ProgressBar mPBLevelRight = null;
        private Activity mActivity = null;
        private boolean mRunning = true;

        @Override
        public void run() {
            while (mRunning) {
                if (mChannelHandle != 0) {
                    updateLevels();
                    try {
                        Thread.sleep(UPDATE_DELTA_MS);
                    } catch (InterruptedException ignored) {
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

        public void setProgressBarLevelLeft(ProgressBar progressBar) {
            this.mPBLevelLeft = progressBar;
        }

        public void setProgressBarLevelRight(ProgressBar progressBar) {
            this.mPBLevelRight = progressBar;
        }

        private void updateLevels() {
            if (mActivity == null) {
                return;
            }
            int level = BASS.BASS_ChannelGetLevel(mChannelHandle);
            if (level == -1) {
                return;
            }
            if (mPBLevelLeft != null) {
                int left = level & 0xFFFF;
                final int pbLeft = ((int) Math.round((1.0 * left / MAX_LEVEL) * 100));
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPBLevelLeft.setProgress(pbLeft);
                    }
                });
            }
            if (mPBLevelRight != null) {
                int right = (level >> 16) & 0xFFFF;
                final int pbRight = ((int) Math.round((1.0 * right / MAX_LEVEL) * 100));
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPBLevelRight.setProgress(pbRight);
                    }
                });
            }
        }
    }
}
