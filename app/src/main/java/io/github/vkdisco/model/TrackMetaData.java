package io.github.vkdisco.model;

import android.graphics.Bitmap;

/**
 * Created by tkaczenko on 11.11.16.
 */
//// FIXME: 11.11.16 Review metadata
public class TrackMetaData {
    private String title;
    private String artist;
    private String year;
    private String album;
    private Bitmap albumArt;

    public TrackMetaData() {

    }

    public TrackMetaData(String title, String artist, String year, String album, Bitmap albumArt) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.album = album;
        this.albumArt = albumArt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }
}
