package io.github.vkdisco.model;

import com.un4seen.bass.BASS;

/**
 * Created by tkaczenko on 10.11.16.
 */

public abstract class Track {
    private TrackMetaData metaData;
    private int channelHandle = 0;
    private OnTrackDataLoadedListener onTrackDataLoadedListener;

    public Track() {

    }

    public Track(TrackMetaData metaData) {
        this.metaData = metaData;
    }

    public Track(TrackMetaData metaData, int channelHandle,
                 OnTrackDataLoadedListener onTrackDataLoadedListener) {
        this.metaData = metaData;
        this.channelHandle = channelHandle;
        this.onTrackDataLoadedListener = onTrackDataLoadedListener;
    }

    abstract public void requestDataLoad();

    abstract public boolean load();

    abstract public boolean isRemote();

    abstract public boolean isCanBeCached();

    public boolean isLoaded() {
        return (channelHandle != 0);
    }

    public void free() {
        BASS.BASS_StreamFree(channelHandle);
    }

    public TrackMetaData getMetaData() {
        return metaData;
    }

    public int getChannelHandle() {
        return channelHandle;
    }

    public void setOnTrackDataLoadedListener(OnTrackDataLoadedListener onTrackDataLoadedListener) {
        this.onTrackDataLoadedListener = onTrackDataLoadedListener;
    }

    public void setMetaData(TrackMetaData metaData) {
        this.metaData = metaData;
    }

    public void setChannelHandle(int channelHandle) {
        this.channelHandle = channelHandle;
    }

    protected OnTrackDataLoadedListener getOnTrackDataLoadedListener() {
        return onTrackDataLoadedListener;
    }

    public interface OnTrackDataLoadedListener {
        void onTrackDataLoaded(boolean success);
    }
}
