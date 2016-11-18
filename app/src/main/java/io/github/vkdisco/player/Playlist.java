package io.github.vkdisco.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import io.github.vkdisco.model.Track;
import io.github.vkdisco.player.interfaces.OnPlaylistChangedListener;

//// TODO: 18.11.16 Implement all methods
public class Playlist {
    private List<Track> tracks = new ArrayList<>();
    private ListIterator<Track> iterator;
    private OnPlaylistChangedListener listener;

    public Playlist(OnPlaylistChangedListener listener) {
        this.listener = listener;
        iterator = tracks.listIterator();
    }

    public void addTrack(Track track) {
        iterator.add(track);
    }

    public void addTracks(Collection<Track> tracks) {
        for (Track track :
                tracks) {
            iterator.add(track);
        }
    }

    public Track removeTrack(int index) {
        return tracks.remove(index);
    }

    public boolean swap(int indexA, int indexB) {

        return false;
    }

    public String serialize() {

        return null;
    }

    public boolean deserialize(String serialized) {

        return false;
    }

    public boolean hasPreviousTrack() {
        return iterator.hasPrevious();
    }

    public Track getPreviousTrack() {
        return iterator.previous();
    }

    public Track getCurrentTrack() {
        return tracks.get(iterator.nextIndex() - 1);
    }

    public int getCurrentTrackIndex() {
        return iterator.nextIndex() - 1;
    }

    public boolean hasNextTrack() {
        return iterator.hasNext();
    }

    public Track getNextTrack() {
        return iterator.next();
    }

    public Track getTrack(int index) {
        return null;
    }

    public Track playTrack(int index) {
        iterator = tracks.listIterator(index);
        return tracks.get(index);
    }
}
