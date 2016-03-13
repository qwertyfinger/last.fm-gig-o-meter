package com.qwertyfinger.lastfmgig_o_meter.data.local.sax.converter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class SaxResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private Class<T> mResponseClass;

    SaxResponseBodyConverter(Class<T> responseClass) {
        this.mResponseClass = responseClass;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {

            /*if (mResponseClass == ReleaseList.class) {
                MusicBrainzParser<T> mbParser = new MusicBrainzParser<>();
                return mbParser.parse(value.byteStream());
            }

            LastFmParser<T> lastFmParser = new LastFmParser<>();
            return lastFmParser.parse(value.byteStream(), mResponseClass);*/
            return null;

        } finally {
            value.close();
        }
    }
}
