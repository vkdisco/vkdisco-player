package io.github.vkdisco.player;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import io.github.vkdisco.model.Track;


/**
 * Created by tkaczenko on 19.11.16.
 */
public class PlaylistTest {
    private Playlist playlist;
    //Create Playlist object
    @Before
    public void setUp() throws Exception {
        playlist = new Playlist(null);
    }
    //Test of iterator's place change by adding 1 track
    @Test
    public void testAddTrack() throws Exception {

        playlist.addTrack(null);
        playlist.addTrack(null);
        assertEquals("Test passed", 1, playlist.getCurrentTrackIndex());

    }
    //Test of iterator's place change by adding collection of tracks
    @Test
    public void testAddTracks() throws Exception{
        List<Track> tracks = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            tracks.add(null);
        }
        playlist.addTracks(tracks);
        assertEquals("Test passed", 4, playlist.getCurrentTrackIndex());

    }
    //Test of removing 2nd track in the playlist
    @Test
    public void TestRemoveTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.removeTrack(1);
        assertNotSame("Test passed", track2, playlist.getTrack(1));
    }
    //Test of swapping 2 tracks
    @Test
    public void testSwap() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.swap(0, 1);
        assertSame("Test passed", track2, playlist.getTrack(0));
    }

    @Test
    public void testSerialize() throws Exception {

    }

    @Test
    public void testDeserialize() throws Exception {

    }

    //Test of situation when previous track exists
    @Test
    public void testHasPreviousTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertEquals("Test passed", true, playlist.hasPreviousTrack());
    }

    //Test of situation when previous track doesn't exist
    @Test
    public void testNotHasPreviousTrack() throws Exception {
        Track track1 = null;
        playlist.addTrack(track1);
        assertEquals("Test passed", false, playlist.hasPreviousTrack());
    }

    //Test of getting previous track
    @Test
    public void testGetPreviousTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertSame("Test passed", track1, playlist.getPreviousTrack());
    }

    //Test of getting current track
    @Test
    public void testGetCurrentTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertSame("Test passed", track2, playlist.getCurrentTrack());
    }

    //Test of getting current track's index
    @Test
    public void testGetCurrentTrackIndex() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertEquals("Test passed", 1, playlist.getCurrentTrackIndex());
    }

    //Test of situation when next track exists
    @Test
    public void testHasNextTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.playTrack(0);
        assertEquals("Test passed", true, playlist.hasNextTrack());
    }

    //Test of situation when next track doesn't exist
    @Test
    public void testNotHasNextTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertEquals("Test passed", false, playlist.hasNextTrack());
    }

    //Test of getting next track
    @Test
    public void testGetNextTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.playTrack(0);
        assertSame("Test passed", track2, playlist.getNextTrack());
    }

    //Test of getting track by index
    @Test
    public void testGetTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        Track track3 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        assertSame("Test passed", track2, playlist.getTrack(1));

    }
    //Test of moving index to the track that need to be played
    @Test
    public void testPlayTrack() throws Exception {
        Track track1 = null;
        Track track2 = null;
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.playTrack(0);
        assertEquals("Test passed", 0, playlist.getCurrentTrackIndex());

    }

}