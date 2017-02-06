/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.ui.login;

import com.qwertyfinger.lastfm_gig_o_meter.data.DataManager;
import com.qwertyfinger.lastfm_gig_o_meter.ui.base.BasePresenter;
import com.qwertyfinger.lastfm_gig_o_meter.util.Utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class LoginPresenter extends BasePresenter<LoginMvpView> {

  private final DataManager mDataManager;
  private final CompositeSubscription mCompositeSubscription;

  public LoginPresenter() {
    this.mDataManager = DataManager.getInstance();
    mCompositeSubscription = new CompositeSubscription();
  }

  @Override public void attachView(LoginMvpView mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (mCompositeSubscription.hasSubscriptions()) mCompositeSubscription.unsubscribe();
  }

  public Observable<Boolean> handleInput(String username) {
    return Observable.create(subscriber -> {
      if (getUsername().equals(username)) {
        if (getMvpView() != null) getMvpView().dismissDialog();
        subscriber.onCompleted();
      } else if (Utils.isConnected()) {
        mDataManager.checkUser(username)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError(e -> Timber.e(e, e.getClass().getCanonicalName()))
            .subscribe(result -> {
              if (result) {
                mDataManager.setUsername(username);
                mDataManager.setArtistsLimit(50);
                subscriber.onNext(true);
              } else if (getMvpView() != null) getMvpView().showWrongUsernameMessage();
            }, e -> {
              if (getMvpView() != null) getMvpView().showErrorToast();
            }, subscriber::onCompleted);
      } else {
        if (getMvpView() != null) getMvpView().showNoConnectionToast();
        subscriber.onCompleted();
      }
    });
  }

  public String getUsername() {
    return mDataManager.getUsername();
  }
}
