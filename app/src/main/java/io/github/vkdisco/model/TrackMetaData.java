package io.github.vkdisco.model;

import android.graphics.Bitmap;

import java.util.Locale;

/**
 * Created by tkaczenko on 11.11.16.
 */

public class TrackMetaData {
    private String title;
    private String artist;
    private String year;
    private String album;
    private Bitmap albumArt;
    private long duration;

    public TrackMetaData() {

    }

    public TrackMetaData(
            String title, String artist, long duration
    ) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public TrackMetaData(
            String title, String artist, String year, String album, Bitmap albumArt, long duration
    ) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.album = album;
        this.albumArt = albumArt;
        this.duration = duration;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTime() {
        Long minutes = ((duration / 1000) % 3600) / 60;
        Long seconds = duration % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
