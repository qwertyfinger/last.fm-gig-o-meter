package com.qwertyfinger.lastfmgig_o_meter.data.local.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.qwertyfinger.lastfmgig_o_meter.data.model.db.TrackDb;

public class TrackContract {

    private TrackContract(){}

    public static final String TABLE_NAME = "track";
    public static final String COLUMN_NAME = "track_name";
    public static final String COLUMN_ARTIST = "artist_name";
    public static final String COLUMN_ARTIST_PLAYCOUNT = "artist_playcount";
    public static final String COLUMN_USER_PLAYCOUNT = "user_playcount";
    public static final String COLUMN_PROBABILITY = "probability";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_IS_LOVED = "is_loved";

    public static final String CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME + " VARCHAR(255)," + COLUMN_ARTIST +
                    " VARCHAR(255) NOT NULL," + COLUMN_ARTIST_PLAYCOUNT + " BIGINT DEFAULT 0," +
                    COLUMN_USER_PLAYCOUNT + " BIGINT DEFAULT 0," + COLUMN_PROBABILITY + " DOUBLE DEFAULT 0," +
                    COLUMN_WEIGHT + " DOUBLE DEFAULT 0," +
                    COLUMN_IS_LOVED + " BOOLEAN DEFAULT FALSE, PRIMARY KEY(" + COLUMN_NAME + "," +
                    COLUMN_ARTIST + ")," + "FOREIGN KEY(" + COLUMN_ARTIST + ") REFERENCES artist(artist_name)" +
                    " ON UPDATE CASCADE ON DELETE CASCADE);" /* +
                    "CREATE TRIGGER on_delete_trigger" +
                    " AFTER DELETE ON " + ArtistContract.TABLE_NAME +
                    " BEGIN" +
                    " DELETE FROM " + TABLE_NAME + " WHERE " + TABLE_NAME + "." + COLUMN_ARTIST + "" +
                    "=OLD." + COLUMN_ARTIST + ";" +
                    " END;"*/;

    public static ContentValues toContentValues(TrackDb track) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, track.getName());
        values.put(COLUMN_ARTIST, track.getArtistName());
        values.put(COLUMN_ARTIST_PLAYCOUNT, track.getArtistPlaycount());
        values.put(COLUMN_USER_PLAYCOUNT, track.getUserPlaycount());
        values.put(COLUMN_PROBABILITY, track.getProbability());
        values.put(COLUMN_WEIGHT, track.getWeight());
        values.put(COLUMN_IS_LOVED, track.isLoved());
        return values;
    }

    public static TrackDb parseCursor(Cursor cursor) {
        TrackDb track = new TrackDb();
        track.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        track.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST)));
        track.setArtistPlaycount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_PLAYCOUNT)));
        track.setUserPlaycount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_PLAYCOUNT)));
        track.setProbability(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PROBABILITY)));
        track.setWeight(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
        track.setLoved(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_LOVED)) > 0);
        return track;
    }
}
