package io.github.vkdisco.model;

/**
 * Created by tkaczenko on 11.11.16.
 */

public class VKTrack extends Track {
    private int id;
    private boolean isCached;

    public VKTrack(TrackMetaData metaData, int channelHandle,
                   OnTrackLoadedListener onTrackLoadedListener) {
        super(metaData, channelHandle, onTrackLoadedListener);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }
}
