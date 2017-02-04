package com.qwertyfinger.lastfmgig_o_meter.data.model.db;

import android.support.annotation.NonNull;

public class ArtistDb /*extends BaseObservable*/ implements Comparable<ArtistDb> {

  private String name;
  private String mbid;

  private String imageUrl;

  private int playcount;
  private double compatibility;
  private double averageNmbOfSongs;
  private int nmbOfConcerts;
  private int syncStatus;

  public ArtistDb() {}

  public ArtistDb(String name) {
    this.name = name;
  }

  @Override public int compareTo(@NonNull ArtistDb o) {
    if (playcount < o.playcount) return -1;
    if (playcount > o.playcount) return 1;

    return name.compareTo(o.name);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArtistDb artistDb = (ArtistDb) o;

    return name.equalsIgnoreCase(artistDb.name);
  }

  @Override public int hashCode() {
    return name.hashCode();
  }

  @Override public String toString() {
    return "ArtistDb{"
        + "name='"
        + name
        + '\''
        + ", mbid='"
        + mbid
        + '\''
        + ", imageUrl='"
        + imageUrl
        + '\''
        + ", playcount="
        + playcount
        + ", compatibility="
        + compatibility
        + ", averageNmbOfSongs="
        + averageNmbOfSongs
        + ", nmbOfConcerts="
        + nmbOfConcerts
        + '}';
  }

  public String getMbid() {
    return mbid;
  }

  public void setMbid(String mbid) {
    this.mbid = mbid;
  }

  //    @Bindable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  //    @Bindable
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  //    @Bindable
  public int getPlaycount() {
    return playcount;
  }

  public void setPlaycount(int playcount) {
    this.playcount = playcount;
  }

  //    @Bindable
  public double getCompatibility() {
    return compatibility;
  }

  public void setCompatibility(double compatibility) {
    this.compatibility = compatibility;
  }

  public double getAverageNmbOfSongs() {
    return averageNmbOfSongs;
  }

  public void setAverageNmbOfSongs(double averageNmbOfSongs) {
    this.averageNmbOfSongs = averageNmbOfSongs;
  }

  public int getNmbOfConcerts() {
    return nmbOfConcerts;
  }

  public void setNmbOfConcerts(int nmbOfConcerts) {
    this.nmbOfConcerts = nmbOfConcerts;
  }

  public void increasePlaycount(int songPlaycount) {
    playcount += songPlaycount;
  }

  //    @Bindable
  public int getSyncStatus() {
    return syncStatus;
  }

  public void setSyncStatus(int syncStatus) {
    this.syncStatus = syncStatus;
  }
}
