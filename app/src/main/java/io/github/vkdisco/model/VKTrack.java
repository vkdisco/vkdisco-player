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
    private String url;

    public VKTrack(VKApiAudio vkApiAudio) {
        setMetaData(getTrackMetaData(vkApiAudio));
        this.id = vkApiAudio.id;
        this.ownerID = vkApiAudio.owner_id;
    }


    @Override
    public void requestDataLoad() {
        loadDataFromVk();
    }

    @Override
    public boolean load() {
        if (url == null) {
            return false;
        }
        return loadFromUrl(url);
    }

    private void loadDataFromVk() {
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
                        url = vkAudio.url;
                        setDataLoaded(true);
                        if (getOnTrackDataLoadedListener() != null) {
                            getOnTrackDataLoadedListener().onTrackDataLoaded(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean loadFromUrl(String url) {
        int channelHandle = BASS.BASS_StreamCreateURL(url, 0, 0, null, 0);
        setChannelHandle(channelHandle);
        return channelHandle != 0;
    }

    private TrackMetaData getTrackMetaData(VKApiAudio vkAudio) {
        TrackMetaData metaData = new TrackMetaData();
        metaData.setTitle(vkAudio.title);
        metaData.setArtist(vkAudio.artist);
        metaData.setDuration(vkAudio.duration);

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
