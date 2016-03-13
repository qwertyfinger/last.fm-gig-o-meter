package com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm;

import java.util.List;

public class SearchResults {

    private int total;
    private List<ArtistLastFm> results;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ArtistLastFm> getResults() {
        return results;
    }

    public void setResults(List<ArtistLastFm> results) {
        this.results = results;
    }
}
