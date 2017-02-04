package com.qwertyfinger.lastfmgig_o_meter.data.model.db;

public class TrackDb {

  private String name;
  private String artistName;
  private int userPlaycount;
  private int artistPlaycount;
  private double weight;
  private double probability;
  private boolean isLoved;

  private ArtistDb artist;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TrackDb trackDb = (TrackDb) o;

    return name.equalsIgnoreCase(trackDb.name) && artistName.equalsIgnoreCase(trackDb.artistName);
  }

  @Override public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + artistName.hashCode();
    return result;
  }

  @Override public String toString() {
    return "TrackDb{"
        + "name='"
        + name
        + '\''
        + ", artist="
        + artistName
        + ", userPlaycount="
        + userPlaycount
        + ", artistPlaycount="
        + artistPlaycount
        + ", weight="
        + weight
        + ", probability="
        + probability
        + ", isLoved="
        + isLoved
        + '}';
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getUserPlaycount() {
    return userPlaycount;
  }

  public void setUserPlaycount(int userPlaycount) {
    this.userPlaycount = userPlaycount;
  }

  public int getArtistPlaycount() {
    return artistPlaycount;
  }

  public void setArtistPlaycount(int artistPlaycount) {
    this.artistPlaycount = artistPlaycount;
  }

  public boolean isLoved() {
    return isLoved;
  }

  public void setLoved(boolean loved) {
    isLoved = loved;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public double getProbability() {
    return probability;
  }

  public void setProbability(double probability) {
    this.probability = probability;
  }

  public ArtistDb getArtist() {
    return artist;
  }

  public void setArtist(ArtistDb artistName) {
    this.artist = artistName;
  }

  public void incrementArtistPlaycount() {
    artistPlaycount++;
  }

  public String getArtistName() {
    return artistName;
  }

  public void setArtistName(String artistName) {
    this.artistName = artistName;
  }
}
