package com.qwertyfinger.lastfmgig_o_meter.data.local.sax.parser;

import android.sax.Element;
import android.sax.RootElement;
import android.support.v4.util.SimpleArrayMap;
import android.util.Xml;

import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.Album;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.ArtistLastFm;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.ErrorLastFm;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.SearchResults;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.TopArtists;
import com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm.TrackLastFm;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@SuppressWarnings("Convert2MethodRef")
public class LastFmParser<T> extends DefaultHandler {

    private InputStream mInputStream;
    private RootElement mRoot;

    private String mImageSize;
    private TrackLastFm mTrack;

    private ArtistLastFm mArtist;
    private SimpleArrayMap<String, String> mImageMap;

    @SuppressWarnings("unchecked")
    public T parse(InputStream inputStream, Class<T> responseClass) {
        this.mInputStream = inputStream;

        mRoot = new RootElement("lfm");

        if (responseClass == SearchResults.class) {
            return (T) parseSearchResults();
        }

        if (responseClass == ArtistLastFm.class) {
            return (T) parseArtist();
        }

        if (responseClass == Album.class) {
            return (T) parseAlbum();
        }

        if (responseClass == TopArtists.class) {
            return (T) parseTopArtists();
        }

        if (responseClass == ErrorLastFm.class) {
            return (T) parseError();
        }

        return null;
    }

    private ArtistLastFm parseArtist() {

        ArtistLastFm artist = new ArtistLastFm();
        SimpleArrayMap<String, String> imageMap = new SimpleArrayMap<>();

        Element artistXml = mRoot.getChild("artist");

        Element name = artistXml.getChild("name");
        Element id = artistXml.getChild("mbid");
        Element onTour = artistXml.getChild("ontour");
        Element imageXml = artistXml.getChild("image");

        Element wiki = artistXml.getChild("bio");
        Element wikiSummary = wiki.getChild("summary");

        Element stats = artistXml.getChild("stats");
        Element listeners = stats.getChild("listeners");
        Element playcount = stats.getChild("playcount");

        artistXml.setEndElementListener(() -> artist.setImages(imageMap));

        name.setEndTextElementListener(body -> artist.setName(body));
        id.setEndTextElementListener(body -> artist.setId(body));
        onTour.setEndTextElementListener(body -> {
            if (body.equals("1")) {
                artist.setOnTour(true);
            }
        });
        listeners.setEndTextElementListener(body -> artist.setListeners(Integer.valueOf(body)));
        playcount.setEndTextElementListener(body -> artist.setPlaycount(Integer.valueOf(body)));
        wikiSummary.setEndTextElementListener(body -> artist.setWiki(body));

        imageXml.setStartElementListener(attributes -> mImageSize = attributes.getValue("size"));
        imageXml.setEndTextElementListener(body -> imageMap.put(mImageSize, body));

        try {
            Xml.parse(mInputStream, Xml.Encoding.UTF_8, mRoot.getContentHandler());
            return artist;
        } catch (SAXException | IOException e) {
            Timber.e(e, e.getClass().getCanonicalName());
        }

        return null;
    }

    private SearchResults parseSearchResults() {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(mInputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            Timber.e(e, e.getClass().getCanonicalName());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Timber.e(e, e.getClass().getCanonicalName());
                }
            }
        }

        String namespace = "http://a9.com/-/spec/opensearch/1.1/";

        String xmlResponse = sb.toString();
        sb = null;
        xmlResponse = new StringBuilder(xmlResponse)
                .insert(xmlResponse.indexOf("results") + 7, " xmlns:opensearch=\"" + namespace + "\"")
                .toString();

        SearchResults results = new SearchResults();
        List<ArtistLastFm> artistList = new ArrayList<>();

        Element resultsXml = mRoot.getChild("results");
        Element total = resultsXml.getChild(namespace, "totalResults");
        Element artistListXml = resultsXml.getChild("artistmatches");

        Element artistXml = artistListXml.getChild("artist");

        Element name = artistXml.getChild("name");
        Element id = artistXml.getChild("mbid");
        Element imageXml = artistXml.getChild("image");
        Element listeners = artistXml.getChild("listeners");

        total.setEndTextElementListener(body -> results.setTotal(Integer.valueOf(body)));

        artistListXml.setEndElementListener(() -> results.setResults(artistList));

        artistXml.setStartElementListener(attributes -> {
            mArtist = new ArtistLastFm();
            mImageMap = new SimpleArrayMap<>();
        });
        artistXml.setEndElementListener(() -> {
            mArtist.setImages(mImageMap);
            artistList.add(mArtist);
        });

        name.setEndTextElementListener(body -> mArtist.setName(body));
        id.setEndTextElementListener(body -> mArtist.setId(body));
        listeners.setEndTextElementListener(body -> mArtist.setListeners(Integer.valueOf(body)));

        imageXml.setStartElementListener(attributes -> mImageSize = attributes.getValue("size"));
        imageXml.setEndTextElementListener(body -> mImageMap.put(mImageSize, body));

        try {
            Xml.parse(xmlResponse, mRoot.getContentHandler());
            return results;
        } catch (SAXException e) {
            Timber.e(e, e.getClass().getCanonicalName());
        }

        return null;
    }

    private TopArtists parseTopArtists() {

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
        id.setEndTextElementListener(body -> mArtist.setId(body));

        imageXml.setStartElementListener(attributes -> mImageSize = attributes.getValue("size"));
        imageXml.setEndTextElementListener(body -> mImageMap.put(mImageSize, body));

        try {
            Xml.parse(mInputStream, Xml.Encoding.UTF_8, mRoot.getContentHandler());
            return topArtists;
        } catch (SAXException | IOException e) {
            Timber.e(e, e.getClass().getCanonicalName());
        }

        return null;
    }

    private Album parseAlbum() {

        Album album = new Album();
        List<TrackLastFm> trackList = new ArrayList<>();
        SimpleArrayMap<String, String> imageMap = new SimpleArrayMap<>();

        Element albumXml = mRoot.getChild("album");

        Element albumName = albumXml.getChild("name");
        Element albumArtist = albumXml.getChild("artist");
        Element albumId = albumXml.getChild("mbid");
        Element listeners = albumXml.getChild("listeners");
        Element playcount = albumXml.getChild("playcount");

        Element imageXml = albumXml.getChild("image");

        Element wiki = albumXml.getChild("wiki");
        Element wikiSummary = wiki.getChild("summary");

        Element trackListXml = albumXml.getChild("tracks");
        Element trackXml = trackListXml.getChild("track");

        Element trackName = trackXml.getChild("name");
        Element duration = trackXml.getChild("duration");
        Element trackArtist = trackXml.getChild("artist");
        Element artistName = trackArtist.getChild("name");

        albumXml.setEndElementListener(() -> album.setImages(imageMap));

        albumName.setEndTextElementListener(body -> album.setName(body));
        albumArtist.setEndTextElementListener(body -> album.setArtist(body));
        albumId.setEndTextElementListener(body -> album.setId(body));
        listeners.setEndTextElementListener(body -> album.setListeners(Integer.valueOf(body)));
        playcount.setEndTextElementListener(body -> album.setPlaycount(Integer.valueOf(body)));
        wikiSummary.setEndTextElementListener(body -> album.setWiki(body));

        imageXml.setStartElementListener(attributes -> mImageSize = attributes.getValue("size"));
        imageXml.setEndTextElementListener(body -> imageMap.put(mImageSize, body));

        trackListXml.setEndElementListener(() -> album.setTracks(trackList));

        trackXml.setStartElementListener(attributes -> mTrack = new TrackLastFm());
        trackXml.setEndElementListener(() -> trackList.add(mTrack));

        trackName.setEndTextElementListener(body -> mTrack.setName(body));
        duration.setEndTextElementListener(body -> mTrack.setDuration(Integer.valueOf(body)));
        artistName.setEndTextElementListener(body -> mTrack.setArtistName(body));

        try {
            Xml.parse(mInputStream, Xml.Encoding.UTF_8, mRoot.getContentHandler());
            return album;
        } catch (SAXException | IOException e) {
            Timber.e(e, e.getClass().getCanonicalName());
        }

        return null;
    }

    private ErrorLastFm parseError() {
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
    }
}
