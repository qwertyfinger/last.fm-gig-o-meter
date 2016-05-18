package com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm;

import android.support.v4.util.SimpleArrayMap;

import com.qwertyfinger.lastfmgig_o_meter.data.model.db.TrackDb;

public class TrackLastFm {

    private String name;
    private String artistName;
    private String artistMbid;
    private int rank;
    private int playcount;
    private SimpleArrayMap<String, String> artistImages;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPlaycount() {
        return playcount;
    }

    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }

    public TrackDb asTrackDb() {
        if (artistName == null || name == null || artistMbid == null) return null;

        TrackDb track = new TrackDb();

        //noinspection ConstantConditions
        track.setArtist(getArtist().asArtistDb());
        track.setArtistName(artistName);
        track.setName(name);
        track.setUserPlaycount(playcount);

        return track;
    }

   /* public TrackRealm asRealm() {
        TrackRealm trackRealm = new TrackRealm();

        if (artistName == null || name == null || artistMbid == null) return null;

        trackRealm.setArtist(getArtist().asRealm());
        trackRealm.setName(name);
        trackRealm.setDuration(duration);
        trackRealm.setUserPlaycount(playcount);

        return trackRealm;
    }*/

    public String getArtistMbid() {
        return artistMbid;
    }

    public void setArtistMbid(String artistMbid) {
        this.artistMbid = artistMbid;
    }

    public SimpleArrayMap<String, String> getArtistImages() {
        return artistImages;
    }

    public void setArtistImages(SimpleArrayMap<String, String> artistImages) {
        this.artistImages = artistImages;
    }

    private ArtistLastFm getArtist() {
        if (artistName == null || artistMbid == null) return null;
        ArtistLastFm artist = new ArtistLastFm();
        artist.setMbid(artistMbid);
        artist.setName(artistName);
        artist.setImages(artistImages);
        return artist;
    }
}
