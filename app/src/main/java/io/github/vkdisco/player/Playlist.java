package io.github.vkdisco.player;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.jsonadapter.TrackAdapter;
import io.github.vkdisco.player.interfaces.OnPlaylistChangedListener;

/*
Several methods will return boolean as successful flag, but now its don't do it
 */

/**
 * Playlist implementation using
 *
 * @see List
 * @see ListIterator
 */
//// TODO: 19.11.16 Test this class
public class Playlist {
    private static final GsonBuilder gsonBuilder;
    private static final Type type = new TypeToken<List<Track>>() {
    }.getType();

    static {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Track.class, new TrackAdapter());
        gsonBuilder.setPrettyPrinting();
    }

    private List<Track> tracks = new ArrayList<>();
    private int index = 0;
    private OnPlaylistChangedListener listener;

    @Deprecated
    private ListIterator<Track> iterator = tracks.listIterator();

    public Playlist(OnPlaylistChangedListener listener) {
        this.listener = listener;
    }

    /**
     * Add one track into list
     *
     * @param track
     * @throws UnsupportedOperationException - if the add method is not supported by this list iterator
     * @throws ClassCastException            - if the class of the specified element prevents it from being added to this list
     * @throws IllegalArgumentException      - if some aspect of this element prevents it from being added to this list
     */
    public void addTrack(Track track) {
        tracks.add(track);
        listener.onPlaylistChanged();
    }

    /**
     * Add Collection<Track> into list
     *
     * @param tracks
     * @throws UnsupportedOperationException - if the add method is not supported by this list iterator
     * @throws ClassCastException            - if the class of the specified element prevents it from being added to this list
     * @throws IllegalArgumentException      - if some aspect of this element prevents it from being added to this list
     */
    public void addTracks(Collection<Track> tracks) {
        for (Track track :
                tracks) {
            this.tracks.add(track);
        }
        listener.onPlaylistChanged();
    }

    /**
     * Remove track from playlist by position
     *
     * @param index index at List
     * @return the track previously at the specified position
     * @throws UnsupportedOperationException - if the remove operation is not supported by this list iterator
     * @throws IllegalStateException         - if neither next nor previous have been called, or remove or add have been called after the last call to next or previous
     */
    public Track removeTrack(int index) {
        if (index < this.index) {
            this.index--;
        }
        return tracks.remove(index);
    }

    public boolean swap(int indexA, int indexB) {
        int size = tracks.size();
        if (indexA > size || indexB > size) {
            return false;
        }
        Collections.swap(tracks, indexA, indexB);
        return true;
    }

    /**
     * Return serialized string for List<Track>
     *
     * @return serialized string
     */
    public String serialize() {
        return gsonBuilder.create().toJson(tracks, type);
    }

    public boolean deserialize(String serialized) {
        this.tracks = gsonBuilder.create().fromJson(serialized, type);
        return !tracks.isEmpty();
    }

    public boolean hasPreviousTrack() {
        int temp = index - 1;
        return !(temp < 0 && temp > tracks.size());
    }

    /**
     * Get previous track
     *
     * @return previous track
     * @throws NoSuchElementException - if the iteration has no previous element
     */
    public Track getPreviousTrack() {
        int temp = index - 1;
        return tracks.get(temp);
    }

    /**
     * Check if iterator change his position
     *
     * @return
     */
    public Track getCurrentTrack() {
        return tracks.get(index);
    }

    /**
     * Check if iterator change his position
     *
     * @return
     */
    public int getCurrentTrackIndex() {
        return index;
    }

    public boolean hasNextTrack() {
        return iterator.hasNext();
    }

    /**
     * Get next track
     *
     * @return previous track
     * @throws NoSuchElementException - if the iteration has no previous element
     */
    public Track getNextTrack() {
        return iterator.next();
    }

    public Track getTrack(int index) {
        return tracks.get(index);
    }

    public Track playTrack(int index) {
        this.index = index;
        return tracks.get(index);
    }
}
