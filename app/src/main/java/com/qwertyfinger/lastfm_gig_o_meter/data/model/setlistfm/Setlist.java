/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.data.model.setlistfm;

import org.threeten.bp.LocalDate;

import java.util.Set;

public class Setlist {

  private LocalDate date;
  private Set<String> songs;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Set<String> getSongs() {
    return songs;
  }

  public void setSongs(Set<String> songs) {
    this.songs = songs;
  }
}
