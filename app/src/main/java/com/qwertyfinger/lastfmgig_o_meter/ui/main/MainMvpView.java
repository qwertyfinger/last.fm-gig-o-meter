/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfmgig_o_meter.ui.main;

import android.support.v4.app.Fragment;

import com.qwertyfinger.lastfmgig_o_meter.ui.base.MvpView;

public interface MainMvpView extends MvpView {

  //    void showSyncInProgress();
  //    void endSync(boolean isSuccessful);
  void showFragment(Fragment fragment, String tag);
}
