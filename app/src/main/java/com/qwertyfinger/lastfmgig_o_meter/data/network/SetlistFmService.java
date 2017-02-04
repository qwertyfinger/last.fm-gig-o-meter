package com.qwertyfinger.lastfmgig_o_meter.data.network;

import com.qwertyfinger.lastfmgig_o_meter.data.model.setlistfm.SetlistResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SetlistFmService {

  String ENDPOINT = "http://api.setlist.fm/rest/0.1/";

  @GET("artist/{mbid}/setlists.xml") Observable<SetlistResult> getSetlists(
      @Path("mbid") String mbid, @Query(value = "p") int page);
}
