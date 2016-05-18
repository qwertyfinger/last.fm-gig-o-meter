package com.qwertyfinger.lastfmgig_o_meter.ui.login;

import com.qwertyfinger.lastfmgig_o_meter.ui.base.MvpView;

public interface LoginMvpView extends MvpView {
    void dismissDialog();
    void showWrongUsernameMessage();
    void showNoConnectionToast();
    void showErrorToast();
}
