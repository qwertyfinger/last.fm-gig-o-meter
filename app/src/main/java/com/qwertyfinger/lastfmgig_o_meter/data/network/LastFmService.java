/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfmgig_o_meter.data.network;

import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.ResponseLastFm;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.TrackList;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface LastFmService {

  String ENDPOINT = "http://ws.audioscrobbler.com/2.0/";

    /*@GET("?method=user.gettopartists")
    Observable<TopArtists> getTopArtists(@Query(value = "api_key") String apiKey,
                                         @Query(value = "user") String user,
                                         @Query(value = "limit") int limit,
                                         @Query(value = "page") int page);*/

    /*@GET("?method=user.gettracks")
    Observable<Response> getTracks(@Query(value = "api_key") String apiKey, @Query(value = "user") String user,
                             @Query(value = "artist") String artist, @Query(value = "limit") int limit,
                             @Query(value = "page") int page);*/

  @GET("?method=user.gettoptracks") Observable<TrackList> getTopTracks(
      @Query(value = "api_key") String apiKey, @Query(value = "user") String user,
      @Query(value = "limit") int limit, @Query(value = "page") int page);

  @GET("?method=user.getlovedtracks") Observable<TrackList> getLovedTracks(
      @Query(value = "api_key") String apiKey, @Query(value = "user") String user,
      @Query(value = "limit") int limit, @Query(value = "page") int page);

  @GET("?method=user.getinfo") Observable<ResponseLastFm> checkUser(
      @Query(value = "api_key") String apiKey, @Query(value = "user") String user);

  //    @GET("?method=user.gettoptracks")
  //    Call<TrackList> getTopTracks(@Query(value = "api_key") String apiKey, @Query(value = "user") String user,
  //                                 @Query(value = "limit") int limit, @Query(value = "page") int page);
}