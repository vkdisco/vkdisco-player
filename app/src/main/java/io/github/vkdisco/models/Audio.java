package io.github.vkdisco.models;

/**
 * Created by tkaczenko on 30.10.16.
 */

public class Audio {
    private int ownerID;
    private int trackID;
    private String title;
    private String url;
    private String artist;
    private String formatTime;
    private int time;

    public Audio(int ownerID, int trackID, String title, String url, String artist, int time) {
        this.ownerID = ownerID;
        this.trackID = trackID;
        this.title = title;
        this.url = url;
        this.artist = artist;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTime() {
        Long min = Long.valueOf((time % 3600) / 60);
        Long sec = Long.valueOf(time % 60);
        if (sec < 10) {
            formatTime = String.valueOf("0" + min + ":0" + sec);
        } else {
            formatTime = String.valueOf("0" + min + ":" + sec);
        }
        return formatTime;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getTrackID() {
        return trackID;
    }

    public void setTrackID(int trackID) {
        this.trackID = trackID;
    }
}
