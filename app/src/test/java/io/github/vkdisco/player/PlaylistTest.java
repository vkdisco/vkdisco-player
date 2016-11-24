package io.github.vkdisco.player;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.model.VKTrack;
import io.github.vkdisco.player.interfaces.OnPlaylistChangedListener;


/**
 * Created by tkaczenko on 19.11.16.
 */
public class PlaylistTest {


    private class  Listener implements OnPlaylistChangedListener{
        public void onPlaylistChanged() {

        }
    }


    private Playlist playlist;
    private VKTrack track1, track2, track3;
    private TrackMetaData trackMeta1 = new TrackMetaData("Nothing Else Matters", "Metallica", 1234),
            trackMeta2 = new TrackMetaData("BYOB", "System of a Down", 5678),
            trackMeta3 = new TrackMetaData("Hell", "Dusturbed", 4464);

    private String serializedString  = "[\n" +
            "  {\n" +
            "    \"id\": 1,\n" +
            "    \"owner_id\": 111,\n" +
            "    \"title\": \"Nothing Else Matters\",\n" +
            "    \"artist\": \"Metallica\",\n" +
            "    \"duration\": 1234\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 2,\n" +
            "    \"owner_id\": 222,\n" +
            "    \"title\": \"BYOB\",\n" +
            "    \"artist\": \"System of a Down\",\n" +
            "    \"duration\": 5678\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 3,\n" +
            "    \"owner_id\": 333,\n" +
            "    \"title\": \"Hell\",\n" +
            "    \"artist\": \"Dusturbed\",\n" +
            "    \"duration\": 4464\n" +
            "  }\n" +
            "]";

    private void createTracks(){
        track1 = new VKTrack(trackMeta1, 1, 111);
        track2 = new VKTrack(trackMeta2, 2, 222);
        track3 = new VKTrack(trackMeta3, 3, 333);

    }

    //Create Playlist object
    @Before
    public void setUp() throws Exception {
        createTracks();
        Listener listener = new Listener();
        playlist = new Playlist(listener);
    }



    //Test of iterator's place change by adding 1 track
    @Test
    public void testAddTrack() throws Exception {

        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertSame("Test passed", track2, playlist.getTrack(1));

    }
    //Test of iterator's place change by adding collection of tracks
    @Test
    public void testAddTracks() throws Exception{
        List<Track> tracks = new ArrayList<>();
        tracks.add(track1);
        tracks.add(track2);
        tracks.add(track3);
        playlist.addTracks(tracks);
        assertEquals("Test passed", 2, playlist.getCurrentTrackIndex());

    }
    //Test of removing 1st track in the playlist
    @Test
    public void TestRemoveTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.removeTrack(0);
        assertSame("Test passed", track2, playlist.getTrack(0));
    }
    //Test of swapping 2 tracks
    @Test
    public void testSwap() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.swap(0, 1);
        assertSame("Test passed", track2, playlist.getTrack(0));
    }

    @Test
    public void testSerialize() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        assertEquals("Test Passed", serializedString, playlist.serialize());

    }

    @Test
    public void testDeserialize() throws Exception {

        assertEquals("Test passed", true, playlist.deserialize(serializedString));

    }

    //Test of situation when previous track exists
    @Test
    public void testHasPreviousTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertEquals("Test passed", true, playlist.hasPreviousTrack());
    }

    //Test of situation when previous track doesn't exist
    @Test
    public void testNotHasPreviousTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.playTrack(0);
        assertEquals("Test passed", false, playlist.hasPreviousTrack());
    }

    //Test of getting previous track
    @Test
    public void testGetPreviousTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        playlist.playTrack(1);
        assertSame("Test passed", track1, playlist.getPreviousTrack());
    }

    //Test of getting current track
    @Test
    public void testGetCurrentTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        playlist.playTrack(1);
        assertSame("Test passed", track2, playlist.getCurrentTrack());
    }

    //Test of getting current track's index
    @Test
    public void testGetCurrentTrackIndex() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        assertEquals("Test passed", 1, playlist.getCurrentTrackIndex());
    }

    //Test of situation when next track exists
    @Test
    public void testHasNextTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.playTrack(0);
        assertEquals("Test passed", true, playlist.hasNextTrack());
    }

    //Test of situation when next track doesn't exist
    @Test
    public void testNotHasNextTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.playTrack(1);
        assertEquals("Test passed", false, playlist.hasNextTrack());
    }

    //Test of getting next track
    @Test
    public void testGetNextTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.playTrack(0);
        assertSame("Test passed", track2, playlist.getNextTrack());
    }

    //Test of getting track by index
    @Test
    public void testGetTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        assertSame("Test passed", track2, playlist.getTrack(1));

    }
    //Test of moving index to the track that need to be played
    @Test
    public void testPlayTrack() throws Exception {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.playTrack(0);
        assertEquals("Test passed", 0, playlist.getCurrentTrackIndex());
    }

}