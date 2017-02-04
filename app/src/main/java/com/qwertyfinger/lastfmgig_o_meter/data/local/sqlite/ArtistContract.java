package com.qwertyfinger.lastfmgig_o_meter.data.local.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;

public class ArtistContract {

  private ArtistContract() {
  }

  public static final String TABLE_NAME = "artist";
  public static final String COLUMN_NAME = "artist_name";
  public static final String COLUMN_MBID = "mbid";
  public static final String COLUMN_IMAGE_URL = "image_url";
  public static final String COLUMN_PLAYCOUNT = "playcount";
  public static final String COLUMN_COMPATIBILITY = "compatibility";
  public static final String COLUMN_AVERAGE_SONGS_NMB = "average_songs_nmb";
  public static final String COLUMN_CONCERTS_NMB = "concerts_nmb";
  public static final String COLUMN_SYNC_STATUS = "sync_status";

  public static final String CREATE = "CREATE TABLE "
      + TABLE_NAME
      + "("
      + COLUMN_NAME
      + " VARCHAR(255) PRIMARY KEY,"
      + COLUMN_MBID
      + " VARCHAR(255) NOT NULL DEFAULT '',"
      + COLUMN_PLAYCOUNT
      + " BIGINT NOT NULL DEFAULT 0,"
      + COLUMN_IMAGE_URL
      + " TEXT,"
      + COLUMN_COMPATIBILITY
      + " DOUBLE NOT NULL DEFAULT 0,"
      + COLUMN_AVERAGE_SONGS_NMB
      + " INTEGER NOT NULL DEFAULT 0,"
      + COLUMN_CONCERTS_NMB
      + " INTEGER NOT NULL DEFAULT 0,"
      + COLUMN_SYNC_STATUS
      + " INTEGER NOT NULL DEFAULT 0);";

  public static ContentValues toContentValues(ArtistDb artist) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_NAME, artist.getName());
    values.put(COLUMN_MBID, artist.getMbid());
    values.put(COLUMN_PLAYCOUNT, artist.getPlaycount());
    values.put(COLUMN_IMAGE_URL, artist.getImageUrl());
    values.put(COLUMN_COMPATIBILITY, artist.getCompatibility() * 100);
    values.put(COLUMN_AVERAGE_SONGS_NMB, artist.getAverageNmbOfSongs());
    values.put(COLUMN_CONCERTS_NMB, artist.getNmbOfConcerts());
    values.put(COLUMN_SYNC_STATUS, artist.getSyncStatus());
    return values;
  }

  public static ArtistDb parseCursor(Cursor cursor) {
    ArtistDb artist = new ArtistDb();
    artist.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
    artist.setMbid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MBID)));
    artist.setPlaycount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYCOUNT)));
    artist.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
    artist.setCompatibility(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COMPATIBILITY)));
    artist.setAverageNmbOfSongs(
        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE_SONGS_NMB)));
    artist.setNmbOfConcerts(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONCERTS_NMB)));
    artist.setSyncStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYNC_STATUS)));
    return artist;
  }
}
