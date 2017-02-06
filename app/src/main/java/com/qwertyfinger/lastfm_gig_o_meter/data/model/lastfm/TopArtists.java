/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.data.model.lastfm;

import java.util.List;

public class TopArtists {

  private int totalPages;
  private int total;
  private List<ArtistLastFm> artists;

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

  public List<ArtistLastFm> getArtists() {
    return artists;
  }

  public void setArtists(List<ArtistLastFm> artists) {
    this.artists = artists;
  }
}
