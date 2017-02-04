package com.qwertyfinger.lastfmgig_o_meter.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.qwertyfinger.lastfmgig_o_meter.LastFmGigometerApp;

public class PreferencesHelper {

  private static final String PREF_FILE_NAME = "lastfm_gigometer_prefs";
  private static final String OVERALL_SCROBBLES = "overall_scrobbles";
  private static final String OVERALL_TRACKS = "overall_tracks";
  private static final String TOP_TRACKS_SCROBBLES = "top_track_scrobbles";
  private static final String USERNAME = "username";
  private static final String ARTISTS_LIMIT = "artists_limit";

  private final SharedPreferences mPref;

  public PreferencesHelper() {
    mPref =
        LastFmGigometerApp.getInstance().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
  }

  public void setOverallScrobbles(int overallScrobbles) {
    putInt(OVERALL_SCROBBLES, overallScrobbles);
  }

  public void setOverallTracks(int overallTracks) {
    putInt(OVERALL_TRACKS, overallTracks);
  }

  public void setTopTracksScrobbles(int topTracksScrobbles) {
    putInt(TOP_TRACKS_SCROBBLES, topTracksScrobbles);
  }

  public void setArtistLimit(int aritstLimit) {
    putInt(ARTISTS_LIMIT, aritstLimit);
  }

  public void setUsername(String username) {
    mPref.edit().putString(USERNAME, username).apply();
  }

  public int getOverallScrobbles() {
    return getInt(OVERALL_SCROBBLES);
  }

  public int getOverallTracks() {
    return getInt(OVERALL_TRACKS);
  }

  public int getTopTracksScrobbles() {
    return getInt(TOP_TRACKS_SCROBBLES);
  }

  public int getArtistsLimit() {
    return getInt(ARTISTS_LIMIT);
  }

  public String getUsername() {
    return mPref.getString(USERNAME, "");
  }

  public double getCoefficient() {
    double averageScrobbles = (double) getInt(OVERALL_SCROBBLES) / getInt(OVERALL_TRACKS);
    return 0.7 * averageScrobbles + 0.3 * (getInt(TOP_TRACKS_SCROBBLES) / 100.0);
  }

  private void putInt(String prefName, int value) {
    mPref.edit().putInt(prefName, value).apply();
  }

  private int getInt(String prefName) {
    return mPref.getInt(prefName, 50);
  }
}
