/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.ui.base;

public abstract class BasePresenter<T extends MvpView> implements Presenter<T> {

  private T mMvpView;

  @Override public void attachView(T mvpView) {
    mMvpView = mvpView;
  }

  @Override public void detachView() {
    mMvpView = null;
  }

  public boolean isViewAttached() {
    return mMvpView != null;
  }

  public T getMvpView() {
    return mMvpView;
  }

  public void checkViewAttached() {
    if (!isViewAttached()) throw new MvpViewNotAttachedException();
  }

  public static class MvpViewNotAttachedException extends RuntimeException {
    public MvpViewNotAttachedException() {
      super(
          "Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter");
    }
  }
}