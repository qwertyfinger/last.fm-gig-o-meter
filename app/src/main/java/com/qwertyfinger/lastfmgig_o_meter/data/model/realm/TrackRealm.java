package com.qwertyfinger.lastfmgig_o_meter.data.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class TrackRealm extends RealmObject {

    @Required private String name;
    @Required private String artistName;
    private int duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

}
