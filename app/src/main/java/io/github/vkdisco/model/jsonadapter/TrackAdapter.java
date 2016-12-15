package io.github.vkdisco.model.jsonadapter;

import android.util.Log;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.github.vkdisco.model.FileTrack;
import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.model.VKTrack;
import io.github.vkdisco.util.BitmapUtil;

/**
 * Created by tkaczenko on 30.11.16.
 */

public class TrackAdapter extends TypeAdapter<Track> {
    @Override
    public void write(JsonWriter out, Track value) throws IOException {
        TrackMetaData metaData = value.getMetaData();
        if (value instanceof FileTrack) {
            FileTrack fileTrack = (FileTrack) value;
            out.beginObject();
            out.name("type").value(fileTrack.getClass().getSimpleName());
            out.name("path").value(fileTrack.getPath());
            out.name("title").value(metaData.getTitle());
            out.name("artist").value(metaData.getArtist());
            out.name("year").value(metaData.getYear());
            out.name("album").value(metaData.getAlbum());
            if (metaData.getAlbumArt() != null) {
                out.name("album_art").value(BitmapUtil.toString(metaData.getAlbumArt()));
            }
            out.name("duration").value(metaData.getDuration());
            out.endObject();
        }
        if (value instanceof VKTrack) {
            VKTrack vkTrack = (VKTrack) value;
            out.beginObject();
            out.name("type").value(vkTrack.getClass().getSimpleName());
            out.name("id").value(vkTrack.getId());
            out.name("owner_id").value(vkTrack.getOwnerID());
            out.name("title").value(vkTrack.getMetaData().getTitle());
            out.name("artist").value(vkTrack.getMetaData().getArtist());
            out.name("duration").value(vkTrack.getMetaData().getDuration());
            out.endObject();
        }
    }

    @Override
    public Track read(JsonReader in) throws IOException {
        final TrackMetaData metaData = new TrackMetaData();
        String path = null;
        Integer id = null, ownerID = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "type":
                    in.nextString();
                    break;
                case "path":
                    path = in.nextString();
                    break;
                case "title":
                    metaData.setTitle(in.nextString());
                    break;
                case "artist":
                    metaData.setArtist(in.nextString());
                    break;
                case "year":
                    metaData.setYear(in.nextString());
                    break;
                case "album":
                    metaData.setAlbum(in.nextString());
                    break;
                case "album_art":
                    metaData.setAlbumArt(BitmapUtil.toBitmap(in.nextString()));
                    break;
                case "id":
                    id = in.nextInt();
                    break;
                case "owner_id":
                    ownerID = in.nextInt();
                    break;
                case "duration":
                    metaData.setDuration(in.nextLong());
                    break;
            }
        }
        in.endObject();

        if (path != null) {
            return new FileTrack(path);
        } else if (id != null && ownerID != null) {
            return new VKTrack(metaData, id, ownerID);
        } else {
            return null;
        }
    }
}
