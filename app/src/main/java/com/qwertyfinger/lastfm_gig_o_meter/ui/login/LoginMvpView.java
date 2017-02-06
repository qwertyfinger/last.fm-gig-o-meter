/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.ui.login;

import com.qwertyfinger.lastfm_gig_o_meter.ui.base.MvpView;

public interface LoginMvpView extends MvpView {
  void dismissDialog();

  void showWrongUsernameMessage();

  void showNoConnectionToast();

  void showErrorToast();
}
