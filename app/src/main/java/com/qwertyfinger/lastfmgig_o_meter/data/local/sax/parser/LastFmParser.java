package com.qwertyfinger.lastfmgig_o_meter.data.local.sax.parser;

import android.sax.Element;
import android.sax.RootElement;
import android.support.v4.util.SimpleArrayMap;
import android.util.Xml;

import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.ResponseLastFm;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.TrackLastFm;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.TrackList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@SuppressWarnings("Convert2MethodRef") public class LastFmParser<T> extends DefaultHandler {

  private InputStream mInputStream;
  private RootElement mRoot;

  private String mImageSize;

  //    private ArtistLastFm mArtist;
  private SimpleArrayMap<String, String> mImageMap;
  private TrackLastFm mTrack;

  @SuppressWarnings("unchecked") public T parse(InputStream inputStream, Class<T> responseClass) {
    this.mInputStream = inputStream;

    mRoot = new RootElement("lfm");

    if (responseClass == TrackList.class) {
      return (T) parseTrackList();
    }

    if (responseClass == ResponseLastFm.class) {
      return (T) parseResponse();
    }

        /*if (responseClass == ErrorLastFm.class) {
            return (T) parseError();
        }

        if (responseClass == TopArtists.class) {
            return (T) parseTopArtists();
        }*/

    return null;
  }

    /*private TopArtists parseTopArtists() {

        TopArtists topArtists = new TopArtists();
        List<ArtistLastFm> artistList = new ArrayList<>();

        Element topArtistsXml = mRoot.getChild("topartists");

        Element artistXml = topArtistsXml.getChild("artist");

        Element name = artistXml.getChild("name");
        Element id = artistXml.getChild("mbid");

        Element imageXml = artistXml.getChild("image");

        topArtistsXml.setStartElementListener(attributes -> {
            topArtists.setTotal(Integer.valueOf(attributes.getValue("total")));
            topArtists.setTotalPages(Integer.valueOf(attributes.getValue("totalPages")));
        });
        topArtistsXml.setEndElementListener(() -> topArtists.setArtists(artistList));

        artistXml.setStartElementListener(attributes -> {
            mArtist = new ArtistLastFm();
            mImageMap = new SimpleArrayMap<>();
        });
        artistXml.setEndElementListener(() -> {
            mArtist.setImages(mImageMap);
            artistList.add(mArtist);
        });

        name.setEndTextElementListener(body -> mArtist.setName(body));
        id.setEndTextElementListener(body -> mArtist.setMbid(body));

        imageXml.setStartElementListener(attributes -> mImageSize = attributes.getValue("size"));
        imageXml.setEndTextElementListener(body -> mImageMap.put(mImageSize, body));

        try {
            Xml.parse(mInputStream, Xml.Encoding.UTF_8, mRoot.getContentHandler());
            return topArtists;
        } catch (SAXException | IOException e) {
            Timber.e(e, e.getClass().getCanonicalName());
        }
        return null;
    }*/

  private TrackList parseTrackList() {
    TrackList result = new TrackList();
    List<TrackLastFm> trackList = new ArrayList<>();

    //        TOP TRACKS
    Element topTracks = mRoot.getChild("toptracks");
    Element track = topTracks.getChild("track");
    Element trackName = track.getChild("name");
    Element playcount = track.getChild("playcount");
    Element imageXml = track.getChild("image");

    Element artist = track.getChild("artist");
    Element artistName = artist.getChild("name");
    Element artistId = artist.getChild("mbid");

    topTracks.setStartElementListener(attributes -> {
      result.setTotal(Integer.valueOf(attributes.getValue("total")));
      result.setTotalPages(Integer.valueOf(attributes.getValue("totalPages")));
    });
    topTracks.setEndElementListener(() -> result.setTracks(trackList));

    track.setStartElementListener(attributes -> {
      mTrack = new TrackLastFm();
      mTrack.setRank(Integer.valueOf(attributes.getValue("rank")));
      mImageMap = new SimpleArrayMap<>();
    });
    track.setEndElementListener(() -> {
      mTrack.setArtistImages(mImageMap);
      trackList.add(mTrack);
    });

    trackName.setEndTextElementListener(body -> mTrack.setName(body));
    playcount.setEndTextElementListener(body -> mTrack.setPlaycount(Integer.valueOf(body)));
    artistName.setEndTextElementListener(body -> mTrack.setArtistName(body));
    artistId.setEndTextElementListener(body -> mTrack.setArtistMbid(body));

    imageXml.setStartElementListener(attributes -> mImageSize = attributes.getValue("size"));
    imageXml.setEndTextElementListener(body -> mImageMap.put(mImageSize, body));

    //        LOVED TRACKS
    Element lovedTracks = mRoot.getChild("lovedtracks");

    Element lovedTrack = lovedTracks.getChild("track");
    Element lovedTrackName = lovedTrack.getChild("name");

    Element lovedArtist = lovedTrack.getChild("artist");
    Element lovedArtistName = lovedArtist.getChild("name");
    Element lovedArtistId = lovedArtist.getChild("mbid");

    lovedTracks.setStartElementListener(attributes -> {
      result.setTotal(Integer.valueOf(attributes.getValue("total")));
      result.setTotalPages(Integer.valueOf(attributes.getValue("totalPages")));
    });
    lovedTracks.setEndElementListener(() -> result.setTracks(trackList));

    lovedTrack.setStartElementListener(attributes -> {
      mTrack = new TrackLastFm();
      mImageMap = new SimpleArrayMap<>();
    });
    lovedTrack.setEndElementListener(() -> {
      mTrack.setArtistImages(mImageMap);
      trackList.add(mTrack);
    });

    lovedTrackName.setEndTextElementListener(body -> mTrack.setName(body));
    lovedArtistName.setEndTextElementListener(body -> mTrack.setArtistName(body));
    lovedArtistId.setEndTextElementListener(body -> mTrack.setArtistMbid(body));

    try {
      Xml.parse(mInputStream, Xml.Encoding.UTF_8, mRoot.getContentHandler());
      return result;
    } catch (SAXException | IOException e) {
      Timber.e(e, e.getClass().getCanonicalName());
    }
    return null;
  }

    /*private ErrorLastFm parseError() {
        ErrorLastFm error = new ErrorLastFm();

        Element errorXml = mRoot.getChild("error");

        errorXml.setStartElementListener(attributes ->
                error.setCode(Integer.valueOf(attributes.getValue("code"))));
        errorXml.setEndTextElementListener(body -> error.setDescription(body));

        try {
            Xml.parse(mInputStream, Xml.Encoding.UTF_8, mRoot.getContentHandler());
            return error;
        } catch (SAXException | IOException e) {
            Timber.e(e, e.getClass().getCanonicalName());
        }
        return null;
    }*/

  private ResponseLastFm parseResponse() {
    ResponseLastFm response = new ResponseLastFm();
    mRoot.setStartElementListener(attributes -> response.setStatus(attributes.getValue("status")));

    try {
      Xml.parse(mInputStream, Xml.Encoding.UTF_8, mRoot.getContentHandler());
      return response;
    } catch (SAXException | IOException e) {
      Timber.e(e, e.getClass().getCanonicalName());
    }
    return null;
  }
}
