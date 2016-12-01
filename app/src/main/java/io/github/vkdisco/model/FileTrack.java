package io.github.vkdisco.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.un4seen.bass.BASS;

import java.io.File;

/**
 * Created by tkaczenko on 11.11.16.
 */

public class FileTrack extends Track {
    private static final String LOG_TAG = "FileTrack";
    private final String path;

    public FileTrack(String path) {
        this.path = path;
    }

    @Override
    public void requestDataLoad() {
        setMetaData(getTrackMetaData(path));
        setDataLoaded(true);
        if (getOnTrackDataLoadedListener() != null) {
            getOnTrackDataLoadedListener().onTrackDataLoaded(true);
        }
    }

    @Override
    public boolean load() {
        return loadFromFile();
    }

    private boolean loadFromFile() {
        if (path == null) {
            return false;
        }

        int channelHandle = BASS.BASS_StreamCreateFile(path, 0, 0, 0);
        setChannelHandle(channelHandle);
        return channelHandle != 0;
    }

    private TrackMetaData getTrackMetaData(String path) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        if (path == null) {
            return null;
        }
        try {
            metadataRetriever.setDataSource(path);
        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, "getTrackMetaData: setDataSource() throws IllegalArgumentException!");
            return null;
        }

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
        metaData.setDuration(
                Long.parseLong(
                        metadataRetriever.extractMetadata(
                                MediaMetadataRetriever.METADATA_KEY_DURATION
                        )
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

    public String getPath() {
        return path;
    }
}
