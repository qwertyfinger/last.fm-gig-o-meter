package com.qwertyfinger.lastfmgig_o_meter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class LastFmGigometerApp extends Application {

  private static LastFmGigometerApp sInstance;
  private RefWatcher refWatcher;

  public static LastFmGigometerApp getInstance() {
    return sInstance;
  }

  @Override public void onCreate() {
    super.onCreate();
    if (sInstance == null) sInstance = this;
    AndroidThreeTen.init(this);

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
      LeakCanary.install(this);
    } else {
      Timber.plant(new CrashReportingTree());
    }
  }

  public static RefWatcher getRefWatcher(Context context) {
    LastFmGigometerApp application = (LastFmGigometerApp) context.getApplicationContext();
    return application.refWatcher;
  }

  /** A tree which logs important information for crash reporting. */
  private static class CrashReportingTree extends Timber.Tree {
    @Override protected void log(int priority, String tag, String message, Throwable t) {
      if (priority == Log.VERBOSE || priority == Log.DEBUG) {
        return;
      }

      //            TODO: implement Crashlytics
      //            FakeCrashLibrary.log(priority, tag, message);

      if (t != null) {
        if (priority == Log.ERROR) {
          //                    FakeCrashLibrary.logError(t);
        } else if (priority == Log.WARN) {
          //                    FakeCrashLibrary.logWarning(t);
        }
      }
    }
  }
}
