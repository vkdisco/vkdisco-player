package io.github.vkdisco.model;

import com.un4seen.bass.BASS;

/**
 * Created by tkaczenko on 10.11.16.
 */

public abstract class Track {
    private TrackMetaData metaData;
    private int channelHandle;
    private OnTrackLoadedListener onTrackLoadedListener;

    public Track(TrackMetaData metaData, int channelHandle,
                 OnTrackLoadedListener onTrackLoadedListener) {
        this.metaData = metaData;
        this.channelHandle = channelHandle;
        this.onTrackLoadedListener = onTrackLoadedListener;
    }

    public void loadRequest() {
        //// TODO: 11.11.16 Implement
    }

    public String serialize() {
        //// TODO: 11.11.16 Implement
        return null;
    }

    public String deserialize() {
        //// TODO: 11.11.16 Impelement
        return null;
    }

    public void free() {
        BASS.BASS_StreamFree(channelHandle);
    }

    public boolean isRemote() {
        //// TODO: 11.11.16 Impelement
        return false;
    }

    public boolean isCanBeCached() {
        //// TODO: 11.11.16 Implement
        return false;
    }

    public boolean isOk() {
        //// TODO: 11.11.16 Implement
        return false;
    }

    public interface OnTrackLoadedListener {
        void onLoad(Track track);
    }

    public TrackMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(TrackMetaData metaData) {
        this.metaData = metaData;
    }

    public int getChannelHandle() {
        return channelHandle;
    }

    public void setChannelHandle(int channelHandle) {
        this.channelHandle = channelHandle;
    }

    public OnTrackLoadedListener getOnTrackLoadedListener() {
        return onTrackLoadedListener;
    }

    public void setOnTrackLoadedListener(OnTrackLoadedListener onTrackLoadedListener) {
        this.onTrackLoadedListener = onTrackLoadedListener;
    }
}
