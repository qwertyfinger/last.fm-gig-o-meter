package com.qwertyfinger.lastfmgig_o_meter.data.local.sax.parser;

import android.sax.Element;
import android.sax.RootElement;
import android.util.ArraySet;
import android.util.Xml;

import com.qwertyfinger.lastfmgig_o_meter.data.model.setlistfm.Setlist;
import com.qwertyfinger.lastfmgig_o_meter.data.model.setlistfm.SetlistResult;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class SetlistFmParser<T> extends DefaultHandler {

  private SetlistResult setlistResult;
  private Setlist setlist;
  private Set<String> songs;

  public T parse(InputStream inputStream) {
    List<Setlist> setlistList = new ArrayList<>();
    RootElement root = new RootElement("setlists");
    Element setlistXml = root.getChild("setlist");
    Element sets = setlistXml.getChild("sets");
    Element set = sets.getChild("set");
    Element song = set.getChild("song");

    root.setStartElementListener(attributes -> {
      setlistResult = new SetlistResult();
      setlistResult.setTotal(Integer.parseInt(attributes.getValue("total")));
      setlistResult.setPage(Integer.parseInt(attributes.getValue("page")));
    });
    root.setEndElementListener(() -> setlistResult.setSetlists(setlistList));

    setlistXml.setStartElementListener(attributes -> {
      setlist = new Setlist();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      setlist.setDate(LocalDate.parse(attributes.getValue("eventDate"), formatter));
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        songs = new ArraySet<>();
      } else {
        songs = new HashSet<>();
      }
    });
    setlistXml.setEndElementListener(() -> {
      setlist.setSongs(songs);
      setlistList.add(setlist);
    });

    song.setStartElementListener(attributes -> songs.add(attributes.getValue("name")));

    try {
      Xml.parse(inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
      //noinspection unchecked
      return (T) setlistResult;
    } catch (SAXException | IOException | DateTimeParseException e) {
      Timber.e(e, e.getClass().getCanonicalName());
    }

    return null;
  }
}