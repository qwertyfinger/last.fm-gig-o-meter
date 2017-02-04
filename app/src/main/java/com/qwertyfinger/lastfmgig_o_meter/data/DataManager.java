package com.qwertyfinger.lastfmgig_o_meter.data;

import android.support.v4.util.ArrayMap;
import android.util.ArraySet;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.qwertyfinger.lastfmgig_o_meter.BuildConfig;
import com.qwertyfinger.lastfmgig_o_meter.data.local.DatabaseHelper;
import com.qwertyfinger.lastfmgig_o_meter.data.local.PreferencesHelper;
import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;
import com.qwertyfinger.lastfmgig_o_meter.data.model.db.TrackDb;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.TrackLastFm;
import com.qwertyfinger.lastfmgig_o_meter.data.model.setlistfm.Setlist;
import com.qwertyfinger.lastfmgig_o_meter.data.network.LastFmService;
import com.qwertyfinger.lastfmgig_o_meter.data.network.RetrofitFactory;
import com.qwertyfinger.lastfmgig_o_meter.data.network.SetlistFmService;
import com.qwertyfinger.lastfmgig_o_meter.util.Constants;
import com.qwertyfinger.lastfmgig_o_meter.util.Utils;
import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import timber.log.Timber;

public class DataManager {

  private static DataManager sInstance;
  private DatabaseHelper mDatabaseHelper;
  private final PreferencesHelper mPrefHelper;
  private final LastFmService mLastFmService;
  private final SetlistFmService mSetlistFmService;

  private DataManager() {
    mDatabaseHelper = new DatabaseHelper();
    mPrefHelper = new PreferencesHelper();
    mLastFmService = RetrofitFactory.makeLastFmService();
    mSetlistFmService = RetrofitFactory.makeSetlistFmService();
    createPicasso();
  }

  public static DataManager getInstance() {
    if (sInstance == null) sInstance = new DataManager();
    return sInstance;
  }

  public void setUsername(String username) {
    mPrefHelper.setUsername(username);
  }

  public String getUsername() {
    return mPrefHelper.getUsername();
  }

  public void setArtistsLimit(int limit) {
    mPrefHelper.setArtistLimit(limit);
  }

  public int getArtistsLimit() {
    return mPrefHelper.getArtistsLimit();
  }

  public Observable<Boolean> checkUser(String username) {
    return Observable.create(subscriber -> {
      mLastFmService.checkUser(BuildConfig.LAST_FM_API_KEY, username)
          //                    .retry(1)
          .subscribe(response -> {
            if (response.getStatus().equals(Constants.LASTFM_RESPONSE_OK_STATUS)) {
              subscriber.onNext(true);
            } else {
              subscriber.onNext(false);
            }
          }, e -> {
            if (e instanceof HttpException) {
              Timber.e(e, e.getClass().getCanonicalName());
              HttpException httpException = (HttpException) e;
              if (httpException.response().code() == 400) {
                subscriber.onNext(false);
              } else {
                subscriber.onError(e);
              }
            }
          }, subscriber::onCompleted);
    });
  }

  public Observable<List<TrackDb>> getAllTracks() {
    return mDatabaseHelper.getAllTracks().take(1).retry(2);
  }

  public Observable<List<ArtistDb>> getAllArtists() {
    return mDatabaseHelper.getAllArtists().take(1).retry(2);
  }

  public Observable<Void> deleteArtist(String name) {
    return mDatabaseHelper.deleteArtist(name).retry(2);
  }

  public Observable<Void> clearData() {
    return mDatabaseHelper.clearArtists().retry(2);
  }

  public Observable<Void> syncArtists(List<ArtistDb> artists) {
    return Observable.create(subscriber -> {
      Map<ArtistDb, List<TrackDb>> scrobbleData = new ArrayMap<>(artists.size());
      Observable.from(artists)
          .concatMap(artist -> mDatabaseHelper.getTracksByArtist(artist.getName()).take(1))
          .retry(2)
          .take(artists.size())
          .toList()
          .subscribe(tracks -> {
            for (int i = 0; i < artists.size(); i++) {
              scrobbleData.put(artists.get(i), tracks.get(i));
            }
          }, subscriber::onError, () -> {
            syncWithSetilstFm(scrobbleData, artists.size()).subscribe(aVoid -> {
            }, subscriber::onError);
            calculateCompatibility(scrobbleData);
            mDatabaseHelper.addScrobbleData(scrobbleData).subscribe(aVoid -> {
            }, subscriber::onError, subscriber::onCompleted);
          });
    });
  }

  public Observable<Void> syncData() {
    return Observable.create(subscriber -> {
      //            Instant start = null;
      //            if (BuildConfig.DEBUG) {
      //                start = Instant.now();
      //            }

      int overallScrobbles = 0;
      int topScrobbles = 0;

      List<TrackDb> tracks = new ArrayList<>();
      getLastFmInfo().subscribe(tracks::addAll, subscriber::onError);

      ArrayMap<ArtistDb, List<TrackDb>> scrobbleData = new ArrayMap<>();
      for (int i = 0; i < tracks.size(); i++) {
        TrackDb track = tracks.get(i);

        if (i < 100) topScrobbles += track.getUserPlaycount();
        overallScrobbles += track.getUserPlaycount();

        ArtistDb artist = track.getArtist();
        if (scrobbleData.containsKey(artist)) {
          scrobbleData.get(artist).add(track);
          scrobbleData.keyAt(scrobbleData.indexOfKey(artist))
              .increasePlaycount(track.getUserPlaycount());
        } else {
          String correctedMbid = Utils.getCorrectArtistMbid(artist.getName());
          if (correctedMbid != null) artist.setMbid(correctedMbid);
          artist.increasePlaycount(track.getUserPlaycount());
          scrobbleData.put(artist, new ArrayList<>());
          scrobbleData.get(artist).add(track);
        }
      }

      if (tracks.size() > 110) overallScrobbles -= topScrobbles;
      mPrefHelper.setOverallScrobbles(overallScrobbles);
      mPrefHelper.setOverallTracks(tracks.size());
      mPrefHelper.setTopTracksScrobbles(topScrobbles);

      syncWithSetilstFm(scrobbleData, mPrefHelper.getArtistsLimit()).subscribe(aVoid -> {
      }, subscriber::onError);

      calculateCompatibility(scrobbleData);

      mDatabaseHelper.addScrobbleData(scrobbleData)
          //                    .doOnCompleted(() -> {
          //                        if (BuildConfig.DEBUG) {
          //                            Instant end = Instant.now();
          //                            Timber.i("Finished sync in " + Duration.between(start, end).toMillis() + "ms");
          //                        }
          //                    })
          .subscribe(aVoid -> {
          }, subscriber::onError, subscriber::onCompleted);
    });
  }

  private void calculateCompatibility(Map<ArtistDb, List<TrackDb>> scrobbleData) {
    Iterator<Map.Entry<ArtistDb, List<TrackDb>>> iter = scrobbleData.entrySet().iterator();
    //noinspection WhileLoopReplaceableByForEach
    while (iter.hasNext()) {
      Map.Entry<ArtistDb, List<TrackDb>> entry = iter.next();
      double totalWeight = 0;
      if (entry.getKey().getNmbOfConcerts() > 2) {
        for (int i = 0; i < entry.getValue().size(); i++) {
          totalWeight += entry.getValue().get(i).getWeight();
        }
        entry.getKey().setCompatibility(totalWeight / entry.getKey().getAverageNmbOfSongs());
      }
    }
  }

  private Observable<List<TrackDb>> getLastFmInfo() {
    return Observable.create(subscriber -> {
      //            Instant start = null;
      //            if (BuildConfig.DEBUG) {
      //                start = Instant.now();
      //            }
      String username = getUsername();

      List<TrackDb> tracks = new ArrayList<>();

      //            dealing with top tracks results
      getTopTracks(username).subscribe(result -> {
        //  fix for last.fm bug with 0 playcount in the middle of list
        boolean previousTrackNonEligible = false;

        for (int i = 0; i < result.size(); i++) {
          TrackLastFm track = result.get(i);
          if (track.getPlaycount() < 3) {
            if (previousTrackNonEligible) break;

            previousTrackNonEligible = true;
            continue;
          } else if (previousTrackNonEligible) {
            previousTrackNonEligible = false;
          }
          tracks.add(track.asTrackDb());
        }
      }, subscriber::onError);

      //            dealing with loved tracks results
      getLovedTracks(username).subscribe(lovedTracks -> {
        for (int i = 0; i < lovedTracks.size(); i++) {
          TrackDb lovedTrack = lovedTracks.get(i).asTrackDb();
          if (tracks.contains(lovedTrack)) {
            tracks.get(tracks.indexOf(lovedTrack)).setLoved(true);
          }
        }
      }, e -> Timber.e(e, e.getClass().getCanonicalName()));

      subscriber.onNext(tracks);
      subscriber.onCompleted();

      //            if (BuildConfig.DEBUG) {
      //                Instant end = Instant.now();
      //                Timber.i("Finished overall Last.fm sync in " + Duration.between(start, end).toMillis() + "ms");
      //            }
    });
  }

  private Observable<List<TrackLastFm>> getTopTracks(String username) {
    return Observable.create(subscriber -> {
      //            Instant start = null;
      //            if (BuildConfig.DEBUG) {
      //                start = Instant.now();
      //            }

      List<TrackLastFm> result = new ArrayList<>();
      mLastFmService.getTopTracks(BuildConfig.LAST_FM_API_KEY, username, 1, 1)
          .retry(1)
          .subscribe(stats -> {
            int totalTracks = stats.getTotal();
            double limit;
            if (totalTracks < 20000) {
              limit = totalTracks * 0.25;
            } else {
              limit = 5000;
            }

            Integer numberOfPages = (int) Math.ceil(totalTracks / limit);
            Integer pages[] = new Integer[numberOfPages];
            for (int i = 1; i <= numberOfPages; i++) {
              pages[i - 1] = i;
            }

            Observable.from(pages)
                .concatMap(
                    page -> mLastFmService.getTopTracks(BuildConfig.LAST_FM_API_KEY, username,
                        (int) limit, page))
                .retry(2)
                .takeWhile(trackList -> trackList.getTracks().get(0).getPlaycount() > 2
                    || trackList.getTracks().get(1).getPlaycount() > 2)
                .takeUntil(trackList -> {
                  List<TrackLastFm> tracks = trackList.getTracks();
                  return tracks.get(tracks.size() - 1).getPlaycount() < 3
                      && tracks.get(tracks.size() - 2).getPlaycount() < 3;
                })
                .subscribe(trackList -> result.addAll(trackList.getTracks()), subscriber::onError);
          }, subscriber::onError);
      subscriber.onNext(result);
      subscriber.onCompleted();

      //            if (BuildConfig.DEBUG) {
      //                Instant end = Instant.now();
      //                Timber.i("Finished top tracks sync in " + Duration.between(start, end).toMillis() + "ms");
      //            }
    });
  }

  private Observable<List<TrackLastFm>> getLovedTracks(String username) {
    return Observable.create(subscriber -> {
      //            Instant start = null;
      //            if (BuildConfig.DEBUG) {
      //                start = Instant.now();
      //            }

      List<TrackLastFm> result = new ArrayList<>();
      mLastFmService.getLovedTracks(BuildConfig.LAST_FM_API_KEY, username, 1, 1)
          .retry(1)
          .subscribe(stats -> {
            Integer numberOfPages = (int) Math.ceil(stats.getTotal() / 1000.0);
            Integer pages[] = new Integer[numberOfPages];
            for (int i = 1; i <= numberOfPages; i++) {
              pages[i - 1] = i;
            }

            Observable.from(pages)
                .concatMap(
                    page -> mLastFmService.getLovedTracks(BuildConfig.LAST_FM_API_KEY, username,
                        1000, page))
                .retry(2)
                .subscribe(trackList -> result.addAll(trackList.getTracks()), subscriber::onError);
          }, subscriber::onError);
      subscriber.onNext(result);
      subscriber.onCompleted();

      //            if (BuildConfig.DEBUG) {
      //                Instant end = Instant.now();
      //                Timber.i("Finished loved tracks sync in " + Duration.between(start, end).toMillis() + "ms");
      //            }
    });
  }

  private Observable<Void> syncWithSetilstFm(Map<ArtistDb, List<TrackDb>> scrobbleData,
      int artistsNumber) {
    return Observable.create(subscriber -> {
      //            Instant start = null;
      //            if (BuildConfig.DEBUG) {
      //                start = Instant.now();
      //            }

      Iterator<ArtistDb> iter = new TreeSet<>(scrobbleData.keySet()).descendingIterator();

      for (int i = 0; i < artistsNumber && iter.hasNext(); i++) {
        ArtistDb artist = iter.next();
        if (artist.getMbid().equals("") || artist.getPlaycount() < 3) {
          artist.setSyncStatus(Constants.SYNC_STATUS_NO_INFO);
          continue;
        }
        List<Setlist> setlists = new ArrayList<>();
        mSetlistFmService.getSetlists(artist.getMbid(), 1)
            //                        .retry(2)
            .subscribe(firstPage -> {
              for (int j = 0; j < firstPage.getSetlists().size(); j++) {
                Setlist setlist = firstPage.getSetlists().get(j);
                if (setlist.getDate().plusMonths(12).isAfter(LocalDate.now())
                    && setlist.getSongs().size() > 5) {
                  setlists.add(setlist);
                }
              }

              if (firstPage.getTotal() > 20
                  && setlists.size() < 15
                  && (firstPage.isLastDateMoreRecent(6) || setlists.size() < 9)
                  && firstPage.isLastDateMoreRecent(12)) {

                Integer numberOfPages = (int) Math.ceil(firstPage.getTotal() / 20.0);
                Integer pages[] = new Integer[numberOfPages - 1];
                for (int k = 2; k <= numberOfPages; k++) {
                  pages[k - 2] = k;
                }
                Observable.from(pages)
                    .concatMap(page -> mSetlistFmService.getSetlists(artist.getMbid(), page))
                    .retry(1)
                    .takeUntil(result -> (setlists.size() >= 15 || (!result.isLastDateMoreRecent(6)
                        && setlists.size() >= 9) || !result.isLastDateMoreRecent(12)))
                    .subscribe(result -> {
                      for (int j = 0; j < result.getSetlists().size(); j++) {
                        Setlist setlist = result.getSetlists().get(j);
                        if (setlist.getDate().plusMonths(12).isAfter(LocalDate.now())
                            && setlist.getSongs().size() > 5) {
                          setlists.add(setlist);
                        }
                      }
                    }, e -> {
                      //                                            TODO: react to error accordingly
                      Timber.e(e, e.getClass().getCanonicalName()
                          + ", "
                          + artist.getName()
                          + ", "
                          + artist.getMbid()
                          + ", first page");
                      artist.setSyncStatus(Constants.SYNC_STATUS_FAILED);
                      if (e instanceof HttpException) {
                        HttpException httpException = (HttpException) e;
                        try {
                          Timber.i(e, httpException.response().errorBody().string());
                        } catch (IOException e1) {
                          e1.printStackTrace();
                        }
                      }
                    });
              }
            }, e -> {
              //                            TODO: react to error accordingly
              Timber.e(e, e.getClass().getCanonicalName()
                  + ", "
                  + artist.getName()
                  + ", "
                  + artist.getMbid()
                  + ", first page");
              artist.setSyncStatus(Constants.SYNC_STATUS_FAILED);
              if (e instanceof HttpException) {
                HttpException httpException = (HttpException) e;
                try {
                  Timber.i(e, httpException.response().errorBody().string());
                } catch (IOException e1) {
                  e1.printStackTrace();
                }
              }
            });

        int songsPlayed = 0;
        artist.setNmbOfConcerts(setlists.size());
        if (setlists.size() > 2) {
          List<TrackDb> artistTracks = scrobbleData.get(artist);

          for (int j = 0; j < setlists.size(); j++) {
            Setlist setlist = setlists.get(j);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
              for (int k = 0; k < setlist.getSongs().size(); k++) {
                TrackDb song = new TrackDb();
                song.setName(((ArraySet<String>) setlist.getSongs()).valueAt(k));
                song.setArtistName(artist.getName());
                int songIndex = artistTracks.indexOf(song);
                if (songIndex != -1) artistTracks.get(songIndex).incrementArtistPlaycount();
              }
            } else {
              Iterator<String> songsIter = setlist.getSongs().iterator();
              //noinspection WhileLoopReplaceableByForEach
              while (songsIter.hasNext()) {
                TrackDb song = new TrackDb();
                song.setName(songsIter.next());
                song.setArtistName(artist.getName());
                int songIndex = artistTracks.indexOf(song);
                if (songIndex != -1) artistTracks.get(songIndex).incrementArtistPlaycount();
              }
            }
            songsPlayed += setlist.getSongs().size();
          }
          artist.setAverageNmbOfSongs((double) songsPlayed / artist.getNmbOfConcerts());

          for (int j = 0; j < artistTracks.size(); j++) {
            TrackDb track = artistTracks.get(j);
            track.setProbability((double) track.getArtistPlaycount() / artist.getNmbOfConcerts());
            track.setWeight(
                (track.getUserPlaycount() / mPrefHelper.getCoefficient()) * track.getProbability());
            if (track.isLoved()) {
              track.setWeight(track.getWeight() + track.getWeight() * 0.3);
            }
          }
          artist.setSyncStatus(Constants.SYNC_STATUS_HAS_SCORE);
        } else {
          artist.setSyncStatus(Constants.SYNC_STATUS_NO_INFO);
        }
      }

      subscriber.onCompleted();

      //            if (BuildConfig.DEBUG) {
      //                Instant end = Instant.now();
      //                Timber.i("Finished Setlist.fm sync in " + Duration.between(start, end).toMillis() + "ms");
      //            }
    });
  }

  private void createPicasso() {
    OkHttp3Downloader okHttpDownloader = new OkHttp3Downloader(Utils.getAppContext());
    Picasso.setSingletonInstance(
        new Picasso.Builder(Utils.getAppContext()).downloader(okHttpDownloader).build());
  }

    /*public Observable<?> getCachedRequest(String tag) {
        return cachedRequests.get(tag);
    }

    public void addCachedRequest(Observable<?> request, String tag) {
        cachedRequests.put(tag, request);
    }

    public void deleteCachedRequest(String tag) {
        cachedRequests.remove(tag);
    }*/
}
