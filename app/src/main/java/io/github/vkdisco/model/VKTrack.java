package io.github.vkdisco.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.un4seen.bass.BASS;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by tkaczenko on 11.11.16.
 */

public class VKTrack extends Track {
    private static final GsonBuilder builder;

    static {
        builder = new GsonBuilder();
        builder.registerTypeAdapter(VKTrack.class, new Converter());
    }

    private int id;
    private int ownerID;
    private boolean isCached;

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

    @Override
    public String serialize() {
        Gson gson = builder.create();
        Type VKTrackType = new TypeToken<VKTrack>() {
        }.getType();
        return gson.toJson(this, VKTrackType);
    }

    @Override
    public Track deserialize(String srcPath) {
        Gson gson = builder.create();
        Type fileTrackType = new TypeToken<VKTrack>() {
        }.getType();
        VKTrack vkTrack = null;
        try {
            vkTrack = gson.fromJson(
                    new InputStreamReader(new FileInputStream(srcPath)), fileTrackType
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return vkTrack;
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

    public static final class Converter implements
            JsonSerializer<VKTrack>, JsonDeserializer<VKTrack> {
        public JsonElement serialize(VKTrack src, Type type,
                                     JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("id", src.getId());
            object.addProperty("owner_id", src.getOwnerID());
            object.addProperty("title", src.getMetaData().getTitle());
            object.addProperty("artist", src.getMetaData().getArtist());

/*
            object.addProperty("year", src.getMetaData().getYear());
            object.addProperty("album", src.getMetaData().getAlbum());
            object.addProperty("album_art", BitmapUtil.toString(src.getMetaData().getAlbumArt()));
*/
            return object;
        }

        public VKTrack deserialize(JsonElement json, Type type,
                                   JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            int id = object.get("id").getAsInt();
            int ownerID = object.get("owner_id").getAsInt();
            String title = object.get("title").getAsString();
            String artist = object.get("artist").getAsString();

/*
            String year = object.get("year").getAsString();
            String album = object.get("album").getAsString();
            Bitmap albumArt = BitmapUtil.toBitmap(object.get("album_art").getAsString());
*/
            return new VKTrack(
                    new TrackMetaData(title, artist, null, null, null), id, ownerID
            );
        }
    }
}
