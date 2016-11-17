package io.github.vkdisco.player;

import com.un4seen.bass.BASS;

import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.player.interfaces.OnPlayerStateChangedListener;
import io.github.vkdisco.player.interfaces.OnTrackEndListener;
import io.github.vkdisco.player.interfaces.OnTrackSwitchListener;

/**
 * Player
 * Implements player
 */
@SuppressWarnings("all")
public class Player implements Track.OnTrackDataLoadedListener, OnTrackEndListener {
    private Playlist playlist;
    private Track currentTrack = null;
    private PlayerState state = PlayerState.EMPTY;
    private boolean playAfterDataLoad = false;
    private TrackEndNotifier trackEndNotifier = new TrackEndNotifier();
    private int trackSyncEnd = 0;
    private OnPlayerStateChangedListener stateChangedListener;
    private OnTrackSwitchListener trackSwitchListener;

    public Player() {
        trackEndNotifier.setTrackEndListener(this);
    }

    public void play() {
        playAfterDataLoad = true;
        if (currentTrack == null) {
            if (playlist == null) {
                return;
            }
            switchTrack(playlist.getCurrentTrack());
            return;
        }
        if (!currentTrack.isLoaded()) {
            currentTrack.setOnTrackDataLoadedListener(this);
            setState(PlayerState.WAITING_TRACK_DATA);
            currentTrack.requestDataLoad();
            return;
        }
        startPlaying();
    }

    public void pause() {
        playAfterDataLoad = true;
        if (currentTrack == null) {
            return;
        }
        if (!currentTrack.isLoaded()) {
            return;
        }
        BASS.BASS_ChannelPause(currentTrack.getChannelHandle());
        setState(PlayerState.PAUSED);
    }

    public void stop() {
        playAfterDataLoad = false;
        stopPlaying();
    }

    public PlayerState getState() {
        return state;
    }

    public int getTrackLengthSeconds() {
        if (currentTrack == null) {
            return -1;
        }
        if (!currentTrack.isLoaded()) {
            return -1;
        }
        long bytesLength = BASS.BASS_ChannelGetLength(currentTrack.getChannelHandle(), BASS.BASS_POS_BYTE);
        if (bytesLength == -1) {
            return -1;
        }
        double secondsLength = BASS.BASS_ChannelBytes2Seconds(currentTrack.getChannelHandle(), bytesLength);
        if (secondsLength < 0) {
            return -1;
        }
        return (int) secondsLength;
    }

    public double getPosition() {
        if (currentTrack == null) {
            return 0.0;
        }
        if (!currentTrack.isLoaded()) {
            return 0.0;
        }
        long byteLength = BASS.BASS_ChannelGetLength(currentTrack.getChannelHandle(), BASS.BASS_POS_BYTE);
        long bytePosition = BASS.BASS_ChannelGetPosition(currentTrack.getChannelHandle(), BASS.BASS_POS_BYTE);
        return 1.0 * bytePosition / byteLength;
    }

    public void setPosition(double position) {
        if (currentTrack == null) {
            return;
        }
        if (!currentTrack.isLoaded()) {
            return;
        }
        if (position > 1.0) {
            position = 1.0;
        }
        if (position < 0.0) {
            position = 0.0;
        }
        long byteLength = BASS.BASS_ChannelGetLength(currentTrack.getChannelHandle(), BASS.BASS_POS_BYTE);
        long bytePosition = (long) (byteLength * position);
        BASS.BASS_ChannelSetPosition(currentTrack.getChannelHandle(), bytePosition, BASS.BASS_POS_BYTE);
    }

    public boolean playTrack(int index) {
        if (playlist == null) {
            return false;
        }
        Track track = playlist.playTrack(index);
        if (track == null) {
            return false;
        }
        switchTrack(track);
        return true;
    }

    public void nextTrack() {
        if (playlist == null) {
            return;
        }
        switchTrack(playlist.getNextTrack());
    }

    public void previousTrack() {
        if (playlist == null) {
            return;
        }
        switchTrack(playlist.getPreviousTrack());
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        free();
        this.playlist = playlist;
    }

    public boolean isPlayingRemote() {
        if (currentTrack == null) {
            return false;
        }
        return currentTrack.isLoaded() && currentTrack.isRemote();
    }

    public double getRemoteLoadedPercent() {
        if (currentTrack == null) {
            return 0.0;
        }
        if (!currentTrack.isLoaded()) {
            return 0.0;
        }
        if (!currentTrack.isRemote()) {
            return 0.0;
        }
        long byteLength = BASS.BASS_StreamGetFilePosition(currentTrack.getChannelHandle(), BASS.BASS_FILEPOS_END);
        long byteDownloaded = BASS.BASS_StreamGetFilePosition(currentTrack.getChannelHandle(), BASS.BASS_FILEPOS_DOWNLOAD);
        return 1.0 * byteDownloaded / byteLength;
    }

    public TrackMetaData getMetadata() {
        if (currentTrack == null) {
            return null;
        }
        if (!currentTrack.isLoaded()) {
            return null;
        }
        return currentTrack.getMetaData();
    }

    public void setTrackSwitchListener(OnTrackSwitchListener trackSwitchListener) {
        this.trackSwitchListener = trackSwitchListener;
    }

    public void setStateChangedListener(OnPlayerStateChangedListener stateChangedListener) {
        this.stateChangedListener = stateChangedListener;
    }

    public void free() {
        if (currentTrack == null) {
            return;
        }
        stopPlaying();
        if (trackSyncEnd != 0) {
            BASS.BASS_ChannelRemoveSync(currentTrack.getChannelHandle(), trackSyncEnd);
        }
        currentTrack.free();
        currentTrack = null;
        setState(PlayerState.EMPTY);
    }

    @Override
    public void onTrackDataLoaded(boolean success) {
        if (!success) {
            return;
        }
        if (trackSwitchListener != null) { //Track switched, info loaded
            trackSwitchListener.onTrackSwitch();
        }
        if (!currentTrack.load()) {
            return;
        }
        trackSyncEnd = BASS.BASS_ChannelSetSync(currentTrack.getChannelHandle(),
                BASS.BASS_SYNC_END, 0, trackEndNotifier, null);
        if (playAfterDataLoad) {
            startPlaying();
        }
    }

    @Override
    public void onTrackEnd() {
        nextTrack(); // TODO: 17.11.2016 Delay 20ms
    }

    private void setState(PlayerState state) {
        this.state = state;
        if (stateChangedListener != null) {
            stateChangedListener.onPlayerStateChanged(state);
        }
    }

    private void switchTrack(Track track) {
        free();
        if (track == null) {
            return;
        }
        currentTrack = track;
        setState(PlayerState.WAITING_TRACK_DATA);
        currentTrack.requestDataLoad();
    }

    private void startPlaying() {
        if (currentTrack == null) {
            return;
        }
        if (!currentTrack.isLoaded()) {
            return;
        }
        if (state == PlayerState.PLAYING) {
            return;
        }
        BASS.BASS_ChannelPlay(currentTrack.getChannelHandle(), false);
        setState(PlayerState.PLAYING);
    }

    private void stopPlaying() {
        if (currentTrack == null) {
            return;
        }
        if (!currentTrack.isLoaded()) {
            return;
        }
        if (state == PlayerState.STOPPED) {
            return;
        }
        BASS.BASS_ChannelStop(currentTrack.getChannelHandle());
        BASS.BASS_ChannelSetPosition(currentTrack.getChannelHandle(), 0, BASS.BASS_POS_BYTE);
        setState(PlayerState.STOPPED);
    }

    public static class TrackEndNotifier implements BASS.SYNCPROC {
        private OnTrackEndListener trackEndListener;

        @Override
        public void SYNCPROC(int handle, int channel, int data, Object user) {
            if (trackEndListener != null) {
                trackEndListener.onTrackEnd();
            }
        }

        public void setTrackEndListener(OnTrackEndListener trackEndListener) {
            this.trackEndListener = trackEndListener;
        }
    }
}
