/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfmgig_o_meter.data.local;

import android.database.sqlite.SQLiteDatabase;

import com.qwertyfinger.lastfmgig_o_meter.BuildConfig;
import com.qwertyfinger.lastfmgig_o_meter.data.local.sqlite.ArtistContract;
import com.qwertyfinger.lastfmgig_o_meter.data.local.sqlite.DbOpenHelper;
import com.qwertyfinger.lastfmgig_o_meter.data.local.sqlite.TrackContract;
import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;
import com.qwertyfinger.lastfmgig_o_meter.data.model.db.TrackDb;
import com.qwertyfinger.lastfmgig_o_meter.util.Utils;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DatabaseHelper {

  private final BriteDatabase mDb;

  public DatabaseHelper() {
    mDb = new SqlBrite.Builder().logger(message -> Timber.tag("Database")).build()
        .wrapDatabaseHelper(new DbOpenHelper(Utils.getAppContext()), Schedulers.io());
    if (BuildConfig.DEBUG) mDb.setLoggingEnabled(true);
  }

  public BriteDatabase getBriteDb() {
    return mDb;
  }

  public Observable<Void> addScrobbleData(Map<ArtistDb, List<TrackDb>> scrobbleData) {
    return Observable.create(subscriber -> {
      addArtists(scrobbleData.keySet()).retry(2).subscribe(aVoid -> {}, subscriber::onError, () -> {
            addTracks(scrobbleData.values()).retry(2).subscribe(bVoid -> {}, subscriber::onError,
                subscriber::onCompleted);
          });
    });
  }

  public Observable<List<ArtistDb>> getAllArtists() {
    return mDb.createQuery(ArtistContract.TABLE_NAME, "SELECT  * FROM "
        + ArtistContract.TABLE_NAME
        + " ORDER BY "
        + ArtistContract.COLUMN_SYNC_STATUS
        + " DESC, "
        + ArtistContract.COLUMN_COMPATIBILITY
        + " DESC, "
        + ArtistContract.COLUMN_PLAYCOUNT
        + " DESC, "
        + ArtistContract.COLUMN_NAME
        + " ASC").mapToList(ArtistContract::parseCursor);
  }

  public Observable<Void> deleteArtist(String name) {
    return Observable.create(subscriber -> {
      if (subscriber.isUnsubscribed()) return;
      BriteDatabase.Transaction transaction = mDb.newTransaction();
      try {
        mDb.delete(ArtistContract.TABLE_NAME, ArtistContract.COLUMN_NAME + "=" + name);
        transaction.markSuccessful();
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      } finally {
        transaction.end();
      }
    });
  }

  public Observable<Void> clearArtists() {
    return Observable.create(subscriber -> {
      if (subscriber.isUnsubscribed()) return;
      BriteDatabase.Transaction transaction = mDb.newTransaction();
      try {
        mDb.delete(ArtistContract.TABLE_NAME, null);
        transaction.markSuccessful();
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      } finally {
        transaction.end();
      }
    });
  }

  public Observable<List<TrackDb>> getTracksByArtist(String artistName) {
    return mDb.createQuery(TrackContract.TABLE_NAME, "SELECT  * FROM "
        + TrackContract.TABLE_NAME
        + " WHERE "
        + TrackContract.COLUMN_ARTIST
        + " = ?", artistName).mapToList(TrackContract::parseCursor);
  }

  public Observable<List<TrackDb>> getAllTracks() {
    return mDb.createQuery(TrackContract.TABLE_NAME, "SELECT  * FROM " + TrackContract.TABLE_NAME)
        .mapToList(TrackContract::parseCursor);
  }

  private Observable<Void> addArtists(Set<ArtistDb> artists) {
    return Observable.create(subscriber -> {
      //            Instant start = Instant.now();

      if (subscriber.isUnsubscribed()) return;
      BriteDatabase.Transaction transaction = mDb.newTransaction();
      try {
        Iterator<ArtistDb> iter = artists.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (iter.hasNext()) {
          mDb.insert(ArtistContract.TABLE_NAME, ArtistContract.toContentValues(iter.next()),
              SQLiteDatabase.CONFLICT_REPLACE);
        }
        transaction.markSuccessful();
      } catch (Exception e) {
        subscriber.onError(e);
      } finally {
        transaction.end();
        subscriber.onCompleted();

        //                Instant end = Instant.now();
        //                Timber.i("Added artists in " + Duration.between(start, end).toMillis() + "ms");
      }
    });
  }

  private Observable<Void> addTracks(Collection<List<TrackDb>> tracksList) {
    return Observable.create(subscriber -> {
      //            Instant start = Instant.now();

      if (subscriber.isUnsubscribed()) return;
      BriteDatabase.Transaction transaction = mDb.newTransaction();
      try {
        Iterator<List<TrackDb>> iter = tracksList.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (iter.hasNext()) {
          List<TrackDb> tracks = iter.next();
          for (int i = 0; i < tracks.size(); i++) {
            mDb.insert(TrackContract.TABLE_NAME, TrackContract.toContentValues(tracks.get(i)),
                SQLiteDatabase.CONFLICT_REPLACE);
          }
        }
        transaction.markSuccessful();
      } catch (Exception e) {
        subscriber.onError(e);
      } finally {
        transaction.end();
        subscriber.onCompleted();

        //                Instant end = Instant.now();
        //                Timber.i("Added tracks in " + Duration.between(start, end).toMillis() + "ms");
      }
    });
  }
}
