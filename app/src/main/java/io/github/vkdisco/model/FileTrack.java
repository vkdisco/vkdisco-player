package io.github.vkdisco.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import io.github.vkdisco.util.BitmapUtil;

/**
 * Created by tkaczenko on 11.11.16.
 */

public class FileTrack extends Track {
    private static final GsonBuilder builder;

    static {
        builder = new GsonBuilder();
        builder.registerTypeAdapter(FileTrack.class, new Converter());
    }

    private File file;

    public FileTrack(TrackMetaData metaData, int channelHandle,
                     OnTrackLoadedListener onTrackLoadedListener, File file) {
        super(metaData, channelHandle, onTrackLoadedListener);
        this.file = file;
    }

    public FileTrack(TrackMetaData metaData, int channelHandle,
                     OnTrackLoadedListener onTrackLoadedListener, String path) {
        super(metaData, channelHandle, onTrackLoadedListener);
        this.file = new File(path);
    }

    private FileTrack(TrackMetaData metaData, String path) {
        super(metaData);
        this.file = new File(path);
    }

    @Override
    public void loadRequest() {
        loadFromFile();
        getOnTrackLoadedListener().onLoad(this);
    }

    @Override
    public String serialize() {
        Gson gson = builder.create();
        Type fileTrackType = new TypeToken<FileTrack>() {
        }.getType();
        return gson.toJson(this, fileTrackType);
    }

    @Override
    public Track deserialize(String srcPath) {
        Gson gson = builder.create();
        Type fileTrackType = new TypeToken<FileTrack>() {
        }.getType();
        FileTrack fileTrack = null;
        try {
            fileTrack = gson.fromJson(
                    new InputStreamReader(new FileInputStream(srcPath)), fileTrackType
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileTrack;
    }

    private void loadFromFile() {
        if (file == null) {
            return;
        }

        String filePath = file.getAbsolutePath();
        int channelHandle = BASS.BASS_StreamCreateFile(filePath, 0, 0, 0);
        setChannelHandle(channelHandle);

        setMetaData(getTrackMetaData(filePath));
    }

    private TrackMetaData getTrackMetaData(String path) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        TrackMetaData metaData = new TrackMetaData();
        metaData.setTitle(
                metadataRetriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_TITLE
                )
        );
        metaData.setArtist(
                metadataRetriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_ARTIST
                )
        );
        metaData.setYear(
                metadataRetriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_YEAR
                )
        );
        metaData.setAlbum(
                metadataRetriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_ALBUM
                )
        );

        byte[] artBytes = metadataRetriever.getEmbeddedPicture();
        if (artBytes == null) {
            metaData.setAlbumArt(null);
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
            metaData.setAlbumArt(bitmap);
        }
        return metaData;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean isCanBeCached() {
        return false;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static final class Converter implements
            JsonSerializer<FileTrack>, JsonDeserializer<FileTrack> {
        public JsonElement serialize(FileTrack src, Type type,
                                     JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("path", src.getFile().getAbsolutePath());
            object.addProperty("title", src.getMetaData().getTitle());
            object.addProperty("artist", src.getMetaData().getArtist());
            object.addProperty("year", src.getMetaData().getYear());
            object.addProperty("album", src.getMetaData().getAlbum());
            object.addProperty("album_art", BitmapUtil.toString(src.getMetaData().getAlbumArt()));
            return object;
        }

        public FileTrack deserialize(JsonElement json, Type type,
                                     JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String path = object.get("path").getAsString();
            String title = object.get("title").getAsString();
            String artist = object.get("artist").getAsString();
            String year = object.get("year").getAsString();
            String album = object.get("album").getAsString();
            Bitmap albumArt = BitmapUtil.toBitmap(object.get("album_art").getAsString());
            return new FileTrack(
                    new TrackMetaData(title, artist, year, album, albumArt), path
            );
        }
    }
}
