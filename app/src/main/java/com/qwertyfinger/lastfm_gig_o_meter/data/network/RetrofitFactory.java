/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.data.network;

import com.qwertyfinger.lastfm_gig_o_meter.data.local.sax.converter.SaxConverterFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class RetrofitFactory {

  private RetrofitFactory() {

  }

  public static LastFmService makeLastFmService() {
    Retrofit retrofit = getRetrofitBuilder().baseUrl(LastFmService.ENDPOINT).build();
    return retrofit.create(LastFmService.class);
  }

  public static SetlistFmService makeSetlistFmService() {
    Retrofit retrofit = getRetrofitBuilder().baseUrl(SetlistFmService.ENDPOINT).build();
    return retrofit.create(SetlistFmService.class);
  }

  private static Retrofit.Builder getRetrofitBuilder() {
    Interceptor interceptor = chain -> {
      Request request = chain.request()
          .newBuilder()
          .header("User-Agent", "Last.fm Gig-o-Meter v0.1 (donandrino@gmail.com)")
          .build();

      return chain.proceed(request);
    };

    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    return new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(SaxConverterFactory.create())
        .client(client);
  }
}
