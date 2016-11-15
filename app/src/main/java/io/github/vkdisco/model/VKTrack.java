package io.github.vkdisco.model;

import com.un4seen.bass.BASS;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tkaczenko on 11.11.16.
 */

public class VKTrack extends Track {
    private int id;
    private int ownerID;
    private boolean isCached;

    public VKTrack() {

    }

    public VKTrack(TrackMetaData metaData, int channelHandle,
                   OnTrackLoadedListener onTrackLoadedListener, int id, int ownerID) {
        super(metaData, channelHandle, onTrackLoadedListener);
        this.id = id;
        this.ownerID = ownerID;
    }

    private VKTrack(TrackMetaData metaData, int id, int ownerID) {
        super(metaData);
        this.id = id;
        this.ownerID = ownerID;
    }

    @Override
    public void loadRequest() {
        loadFromURL();
        getOnTrackLoadedListener().onLoad(this);
    }

    private void loadFromURL() {
        VKRequest request = VKApi.audio().getById(VKParameters.from("audios", ownerID + "_" + id));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                JSONArray items;
                JSONObject json;
                VKApiAudio vkAudio;
                try {
                    items = response.json.getJSONArray("response");
                    try {
                        json = items.getJSONObject(0);
                        vkAudio = new VKApiAudio();
                        vkAudio.parse(json);
                        setMetaData(getTrackMetaData(vkAudio));
                        loadFromUrl(vkAudio.url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadFromUrl(String url) {
        int channelHandle = BASS.BASS_StreamCreateURL(url, 0, 0, null, 0);
        setChannelHandle(channelHandle);
    }

    private TrackMetaData getTrackMetaData(VKApiAudio vkAudio) {
        TrackMetaData metaData = new TrackMetaData();
        metaData.setTitle(vkAudio.title);
        metaData.setArtist(vkAudio.artist);
        metaData.setDuration(vkAudio.duration);
/*
        metaData.setYear();
        metaData.setAlbum();
        metaData.setAlbumArt();
*/
        return metaData;
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public boolean isCanBeCached() {
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }
}
