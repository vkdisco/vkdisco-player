package io.github.vkdisco.model;

import com.un4seen.bass.BASS;

/**
 * Created by tkaczenko on 10.11.16.
 */
//// TODO: 11.11.16 Review the all implementations
public abstract class Track {
    private TrackMetaData metaData;
    private int channelHandle = 0;
    private OnTrackLoadedListener onTrackLoadedListener;

    public Track(TrackMetaData metaData) {

    }

    public Track(TrackMetaData metaData, int channelHandle,
                 OnTrackLoadedListener onTrackLoadedListener) {
        this.metaData = metaData;
        this.channelHandle = channelHandle;
        this.onTrackLoadedListener = onTrackLoadedListener;
    }

    abstract public void loadRequest();

    abstract public String serialize();

    abstract public Track deserialize(String srcPath);

    abstract public boolean isRemote();

    abstract public boolean isCanBeCached();

    public boolean isOk() {
        return (channelHandle != 0);
    }

    public interface OnTrackLoadedListener {
        void onLoad(Track track);
    }

    public void free() {
        BASS.BASS_StreamFree(channelHandle);
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
