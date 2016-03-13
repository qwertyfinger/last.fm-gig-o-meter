package com.qwertyfinger.lastfmgig_o_meter.data.local.sax.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class SaxConverterFactory extends Converter.Factory {

    public static SaxConverterFactory create() {
        return new SaxConverterFactory();
    }

    private SaxConverterFactory() {}

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new SaxResponseBodyConverter<>((Class<?>) type);
    }

}
