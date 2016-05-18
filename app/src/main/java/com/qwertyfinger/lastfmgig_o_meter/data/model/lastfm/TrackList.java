package com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm;

import java.util.List;

public class TrackList {

    private int totalPages;
    private int total;
    private List<TrackLastFm> tracks;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TrackLastFm> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackLastFm> tracks) {
        this.tracks = tracks;
    }
}
