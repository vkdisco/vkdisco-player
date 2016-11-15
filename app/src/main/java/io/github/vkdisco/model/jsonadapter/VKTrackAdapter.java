package io.github.vkdisco.model.jsonadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.model.VKTrack;

/**
 * Created by tkaczenko on 15.11.16.
 */

public class VKTrackAdapter extends TypeAdapter<VKTrack> {
    @Override
    public void write(JsonWriter out, VKTrack value) throws IOException {
        out.beginObject();
        out.name("id").value(value.getId());
        out.name("owner_id").value(value.getOwnerID());
        out.name("title").value(value.getMetaData().getTitle());
        out.name("artist").value(value.getMetaData().getArtist());
        out.name("duration").value(value.getMetaData().getDuration());
        out.endObject();
    }

    @Override
    public VKTrack read(JsonReader in) throws IOException {
        final VKTrack vkTrack = new VKTrack();
        final TrackMetaData metaData = new TrackMetaData();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id":
                    vkTrack.setId(in.nextInt());
                    break;
                case "owner_id":
                    vkTrack.setOwnerID(in.nextInt());
                    break;
                case "title":
                    metaData.setTitle(in.nextString());
                    break;
                case "artist":
                    metaData.setArtist(in.nextString());
                    break;
                case "duration":
                    metaData.setDuration(in.nextLong());
                    break;
            }
        }
        in.endObject();

        vkTrack.setMetaData(metaData);
        return vkTrack;
    }
}
