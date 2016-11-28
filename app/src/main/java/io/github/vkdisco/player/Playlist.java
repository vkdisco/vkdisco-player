package io.github.vkdisco.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import io.github.vkdisco.model.FileTrack;
import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.VKTrack;
import io.github.vkdisco.model.jsonadapter.FileTrackAdapter;
import io.github.vkdisco.model.jsonadapter.VKTrackAdapter;
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
//// TODO: 28.11.16 Fix deserialize bug
public class Playlist {
    private static final GsonBuilder gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FileTrack.class, new FileTrackAdapter());
        gsonBuilder.registerTypeAdapter(VKTrack.class, new VKTrackAdapter());
        gsonBuilder.setPrettyPrinting();
    }

    private List<Track> tracks = new ArrayList<>();
    private ListIterator<Track> iterator;
    private OnPlaylistChangedListener listener;

    public Playlist(OnPlaylistChangedListener listener) {
        this.listener = listener;
        iterator = tracks.listIterator();
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
        iterator.add(track);
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
            iterator.add(track);
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
        Gson gson = gsonBuilder.create();
        return gson.toJson(tracks);
    }

    public boolean deserialize(String serialized) {
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<List<Track>>() {
        }.getType();
        this.tracks = gson.fromJson(serialized, type);
        return tracks != null;
    }

    public boolean hasPreviousTrack() {
        return iterator.hasPrevious();
    }

    /**
     * Get previous track
     *
     * @return previous track
     * @throws NoSuchElementException - if the iteration has no previous element
     */
    public Track getPreviousTrack() {
        return iterator.previous();
    }

    /**
     * Check if iterator change his position
     *
     * @return
     */
    public Track getCurrentTrack() {
        return tracks.get(iterator.nextIndex() - 1);
    }

    /**
     * Check if iterator change his position
     *
     * @return
     */
    public int getCurrentTrackIndex() {
        return iterator.nextIndex() - 1;
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
        iterator = tracks.listIterator(index);
        return iterator.next();
    }
}
