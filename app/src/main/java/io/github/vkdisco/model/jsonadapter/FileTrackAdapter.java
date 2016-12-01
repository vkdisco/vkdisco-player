package io.github.vkdisco.model.jsonadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.github.vkdisco.model.FileTrack;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.util.BitmapUtil;

/**
 * Created by tkaczenko on 15.11.16.
 */

public class FileTrackAdapter extends TypeAdapter<FileTrack> {
    @Override
    public void write(JsonWriter out, FileTrack value) throws IOException {
        out.beginObject();
        final TrackMetaData metaData = value.getMetaData();
        out.name("path").value(value.getPath());
        out.name("title").value(metaData.getTitle());
        out.name("artist").value(metaData.getArtist());
        out.name("year").value(metaData.getYear());
        out.name("album").value(metaData.getAlbum());
        out.name("album_art").value(BitmapUtil.toString(metaData.getAlbumArt()));
        out.name("duration").value(metaData.getDuration());
        out.endObject();
    }

    @Override
    public FileTrack read(JsonReader in) throws IOException {
        final TrackMetaData metaData = new TrackMetaData();
        String path = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
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
            }
        }
        in.endObject();

        if (path != null) {
            return new FileTrack(path);
        } else {
            return null;
        }
    }
}
