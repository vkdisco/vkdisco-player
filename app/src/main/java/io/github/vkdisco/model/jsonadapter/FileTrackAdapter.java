package io.github.vkdisco.model.jsonadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
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
        out.name("path").value(value.getFile().getAbsolutePath());
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
        final FileTrack fileTrack = new FileTrack();
        final TrackMetaData metaData = new TrackMetaData();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "path":
                    fileTrack.setFile(new File(in.nextString()));
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

        fileTrack.setMetaData(metaData);
        return null;
    }
}
