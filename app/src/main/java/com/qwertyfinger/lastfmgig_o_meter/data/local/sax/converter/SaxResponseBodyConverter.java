/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfmgig_o_meter.data.local.sax.converter;

import com.qwertyfinger.lastfmgig_o_meter.data.local.sax.parser.LastFmParser;
import com.qwertyfinger.lastfmgig_o_meter.data.local.sax.parser.SetlistFmParser;
import com.qwertyfinger.lastfmgig_o_meter.data.model.setlistfm.SetlistResult;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class SaxResponseBodyConverter<T> implements Converter<ResponseBody, T> {

  private final Class<T> mResponseClass;

  SaxResponseBodyConverter(Class<T> responseClass) {
    this.mResponseClass = responseClass;
  }

  @Override public T convert(ResponseBody value) throws IOException {
    try {
      if (mResponseClass == SetlistResult.class) {
        SetlistFmParser<T> setlistParser = new SetlistFmParser<>();
        return setlistParser.parse(value.byteStream());
      }

      LastFmParser<T> lastFmParser = new LastFmParser<>();
      return lastFmParser.parse(value.byteStream(), mResponseClass);
    } finally {
      value.close();
    }
  }
}
