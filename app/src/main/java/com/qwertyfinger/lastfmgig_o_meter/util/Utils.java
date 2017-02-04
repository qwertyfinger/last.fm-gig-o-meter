package com.qwertyfinger.lastfmgig_o_meter.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.qwertyfinger.lastfmgig_o_meter.LastFmGigometerApp;
import com.qwertyfinger.lastfmgig_o_meter.R;

public class Utils {

  public static Context getAppContext() {
    return LastFmGigometerApp.getInstance().getApplicationContext();
  }

  public static boolean isConnected() {
    ConnectivityManager cm =
        (ConnectivityManager) getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnected();
  }

  public static boolean isWifiConnected() {
    ConnectivityManager cm =
        (ConnectivityManager) getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null
        && netInfo.isConnected()
        && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
  }

  public static boolean checkConnectionSnackbar(View view) {
    if (!isConnected()) {
      if (view != null) showNoConnectionSnackbar(view);
      return false;
    }
    return true;
  }

  public static boolean checkConnectionToast(Context context) {
    if (!isConnected()) {
      if (context != null) showNoConnectionToast(context);
      return false;
    }
    return true;
  }

  public static void showNoConnectionSnackbar(View view) {
    Snackbar.make(view, R.string.no_connection, Snackbar.LENGTH_SHORT).show();
  }

  public static void showNoConnectionToast(Context context) {
    Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
  }

  public static void showShortSnackbar(View view, int message) {
    if (view != null) Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
  }

  public static void showLongSnackbar(View view, int message) {
    if (view != null) Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
  }

  public static void showShortToast(Context context, int message) {
    if (context != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public static void showLongToast(Context context, int message) {
    if (context != null) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }

  public static int getScoreColor(double score) {
    Context context = getAppContext();
    if (score < 10) {
      return ContextCompat.getColor(context, R.color.score_very_low);
    } else if (score < 25) {
      return ContextCompat.getColor(context, R.color.score_low);
    } else if (score < 50) {
      return ContextCompat.getColor(context, R.color.score_medium);
    } else if (score < 70) {
      return ContextCompat.getColor(context, R.color.score_high);
    } else if (score < 90) {
      return ContextCompat.getColor(context, R.color.score_very_high);
    } else if (score < 100) {
      return ContextCompat.getColor(context, R.color.score_super);
    } else {
      return ContextCompat.getColor(context, R.color.score_extreme);
    }
  }

  /**
   * Deals with artists who have similar titles, whose best match in Musicbrainz
   * search represents not the most popular artist for this title.
   *
   * @param artistName artist's name
   * @return new mbid if it needs to be corrected, else {@code null}
   */
  public static String getCorrectArtistMbid(String artistName) {
    switch (artistName.toLowerCase()) {
      case "muse":
        return "9c9f1380-2516-4fc9-a3e6-f9f61941d090";
      case "interpol":
        return "b23e8a63-8f47-4882-b55b-df2c92ef400e";
      case "placebo":
        return "847e8284-8582-4b0e-9c26-b042a4f49e57";
      case "beirut":
        return "0af78501-5647-4c18-9a0d-66ac8789e13b";
      case "marilyn manson":
        return "5dfdca28-9ddc-4853-933c-8bc97d87beec";
      case "the used":
        return "8262d8e4-9137-4bb3-a787-3caabbbc13e9";
      case "darkside":
        return "116b79ab-b049-4299-8fd8-17ac8a18b7f3";
      case "archy marshall":
        return "ac01e9ed-5e42-4016-ac13-52c4df3d66a9";
      case "trust":
        return "b8e3d1ae-5983-4af1-b226-aa009b294111";
      case "veto":
        return "96cf8760-18ee-443e-b950-4a219e55e443";
      case "birdy":
        return "1b8bdee5-783a-4eca-88e5-3e9460739546";
      case "garden city movement":
        return "480ed4c4-a771-401b-bff6-e1d7164f95a5";
      case "brainstorm":
        return "563ace2c-6e94-4b64-b544-40099a96b86d";
      case "michael fassbender":
        return "08a0dd24-60bb-4d06-a95e-43bc2f0f7840";
      case "the soronprfbs":
        return "c928c480-f29f-48e8-92d4-f1ed61557500";
      case "onuka":
        return "fbe2ebe7-30b3-4efe-af11-e0563bb8744a";
      case "museum":
        return "76af8b7c-25d6-4f57-9e6c-66a783dfbae0";
      case "cambriana":
        return "2d0b11e5-a2b6-4e93-a170-c6d5dc650c17";
      case "linus young":
        return "9c89bf23-6196-47be-8f20-0cf538a8bd93";
      case "peter nalitch":
        return "481e697a-2f20-45ed-9f8b-8fed42e573a6";
      case "5 vymir":
        return "61a24b3c-762f-4224-9300-c13304a8c976";
      default:
        return null;
    }
  }
}
