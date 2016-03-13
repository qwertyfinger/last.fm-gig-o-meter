package com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm;

import android.support.v4.util.SimpleArrayMap;

import com.qwertyfinger.lastfmgig_o_meter.data.model.realm.ArtistRealm;

//TODO: think about implementation of artist page caching
public class ArtistLastFm {

    private String name;
    private String id;
    private String wiki;

    private int playcount;
    private int listeners;

    private boolean onTour;

    private SimpleArrayMap<String, String> images;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlaycount() {
        return playcount;
    }

    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getListeners() {
        return listeners;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public boolean isOnTour() {
        return onTour;
    }

    public void setOnTour(boolean onTour) {
        this.onTour = onTour;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public SimpleArrayMap<String, String> getImages() {
        return images;
    }

    public void setImages(SimpleArrayMap<String, String> images) {
        this.images = images;
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

    public ArtistRealm asRealm() {
        ArtistRealm artistRealm = new ArtistRealm();

        if (id == null || name == null) return null;

        artistRealm.setId(id);
        artistRealm.setName(name);
        artistRealm.setSubscribed(true);
        artistRealm.setListeners(listeners);
        artistRealm.setPlaycount(playcount);
        artistRealm.setOnTour(onTour);

        if (wiki != null) artistRealm.setWiki(wiki);

//        TODO: implement screensize-dependent image choosing
        if (images != null) artistRealm.setImageUrl(getExtralargeImage());

        return artistRealm;
    }

}
