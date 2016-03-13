package com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm;

import android.support.v4.util.SimpleArrayMap;

import java.util.List;

//TODO: think about implementation of album page caching
public class Album {

    private String name;
    private String artist;
    private String id;
    private String wiki;

    private int listeners;
    private int playcount;

    private SimpleArrayMap<String, String> images;
    private List<TrackLastFm> tracks;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public int getListeners() {
        return listeners;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public int getPlaycount() {
        return playcount;
    }

    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }

    public SimpleArrayMap<String, String> getImages() {
        return images;
    }

    public void setImages(SimpleArrayMap<String, String> images) {
        this.images = images;
    }

    public List<TrackLastFm> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackLastFm> tracks) {
        this.tracks = tracks;
    }

    public String getSmallImage() {
        return images.get("small");
    }

    public String getMediumImage() {
        return images.get("medium");
    }

    public String getLargeImage() {
        return images.get("large");
    }

    public String getExtralargeImage() {
        return images.get("extralarge");
    }

    public String getMegaImage() {
        return images.get("mega");
    }

}
