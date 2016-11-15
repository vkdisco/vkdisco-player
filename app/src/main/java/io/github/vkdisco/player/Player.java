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

// TODO: 16.11.2016 Implement OnTrackLoadedListener
public class Player {
    private Track currentTrack = null;
//    private Playlist playlist;
    private int trackSyncEnd = 0;
    private TrackEndNotifier trackEndNotifier = new TrackEndNotifier();
    private PlayerState state;
    private OnPlayerStateChangedListener stateChangedListener;
    private OnTrackSwitchListener trackSwitchListener;

    public void play() {
    }

    public void pause() {
    }

    public void stop() {
        if (currentTrack == null) {
            return;
        }
        if (!currentTrack.isOk()) {
            return;
        }
        BASS.BASS_ChannelStop(currentTrack.getChannelHandle());
        BASS.BASS_ChannelSetPosition(currentTrack.getChannelHandle(), 0, BASS.BASS_POS_BYTE);
    }

    public PlayerState getState() {
        return state;
    }

    public int getTrackLengthSeconds() {
        if (currentTrack == null) {
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

    public boolean playTrack(int index) {
        // TODO: 16.11.2016 Implement this
//        stop();
//        freeTrack();
//        trackSwitched();
        return false;
    }

    public double getPosition() {
        if (currentTrack == null) {
            return 0.0;
        }
        if (!currentTrack.isOk()) {
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
        if (!currentTrack.isOk()) {
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

//    public Playlist getPlaylist() {
//        return playlist;
//    }

//    public void setPlaylist(Playlist playlist) {
//        freeTrack();
//        currentTrack = null;
//        this.playlist = playlist;
//        if ()
//    }

    public boolean isPlayingRemote() {
        if (currentTrack == null) {
            return false;
        }
        if (!currentTrack.isOk()) {
            return false;
        }
        return currentTrack.isRemote();
    }

    public double getRemoteLoadedPercent() {
        if (currentTrack == null) {
            return 0.0;
        }
        if (!currentTrack.isOk()) {
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
        if (!currentTrack.isOk()) {
            return null;
        }
        return currentTrack.getMetaData();
    }

    public void free() {
        freeTrack();
    }

    public void nextTrack() {
        // TODO: 16.11.2016 Implement this
    }

    public void previousTrack() {
        // TODO: 16.11.2016 Implement this
    }

    public void setTrackSwitchListener(OnTrackSwitchListener trackSwitchListener) {
        this.trackSwitchListener = trackSwitchListener;
    }

    public void setStateChangedListener(OnPlayerStateChangedListener stateChangedListener) {
        this.stateChangedListener = stateChangedListener;
    }

    // TODO: 16.11.2016 Implement onTrackLoaded(); in onTrackLoaded() set BASS_SYNC_END on channel

    private void setState(PlayerState state) {
        this.state = state;
        if (stateChangedListener != null) {
            stateChangedListener.onPlayerStateChanged(state);
        }
    }

    private void loadTrack() {
        if (currentTrack == null) {
            return;
        }
        if (!currentTrack.isOk()) {
            currentTrack.loadRequest();
        }
    }

    private void freeTrack() {
        if (currentTrack == null) {
            return;
        }
        stop();
        if (trackSyncEnd != 0) {
            BASS.BASS_ChannelRemoveSync(currentTrack.getChannelHandle(), trackSyncEnd);
            trackSyncEnd = 0;
        }
        currentTrack.free();
    }

    private void trackSwitched() {
        if (trackSwitchListener != null) {
            trackSwitchListener.onTrackSwitch();
        }
    }

    public static class TrackEndNotifier implements BASS.SYNCPROC {
        private OnTrackEndListener trackEndListener;

        @Override
        public void SYNCPROC(int handle, int channel, int data, Object user) {
            if (trackEndListener != null) {
                trackEndListener.onTrackEnd();
            }
        }
    }
}
