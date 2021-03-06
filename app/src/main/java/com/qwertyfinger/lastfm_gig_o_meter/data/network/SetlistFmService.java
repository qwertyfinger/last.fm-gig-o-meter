/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.data.network;

import com.qwertyfinger.lastfm_gig_o_meter.data.model.setlistfm.SetlistResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SetlistFmService {

  String ENDPOINT = "http://api.setlist.fm/rest/0.1/";

  @GET("artist/{mbid}/setlists.xml") Observable<SetlistResult> getSetlists(
      @Path("mbid") String mbid, @Query(value = "p") int page);
}
