package io.github.vkdisco.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.un4seen.bass.BASS;

import java.io.File;

/**
 * Created by tkaczenko on 11.11.16.
 */

public class FileTrack extends Track {
    private File file;

    public FileTrack() {

    }

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
//        getOnTrackLoadedListener().onTrackLoaded(true);
    }

    private void loadFromFile() {
        if (file == null) {
            return;
        }

        String filePath = file.getAbsolutePath();
        int channelHandle = BASS.BASS_StreamCreateFile(filePath, 0, 0, 0);
        setChannelHandle(channelHandle);

        setMetaData(getTrackMetaData(filePath));
        if (getOnTrackLoadedListener() != null) {
            getOnTrackLoadedListener().onTrackLoaded(channelHandle != 0);
        }
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
