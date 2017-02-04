package com.qwertyfinger.lastfmgig_o_meter.ui.main;

import com.qwertyfinger.lastfmgig_o_meter.data.DataManager;
import com.qwertyfinger.lastfmgig_o_meter.ui.base.BasePresenter;

import rx.subscriptions.CompositeSubscription;

@SuppressWarnings("unchecked") public class MainPresenter extends BasePresenter<MainMvpView> {

  private final DataManager mDataManager;
  private CompositeSubscription mCompositeSubscription;

  //    private static final String sRequestTag = "syncData";

  public MainPresenter() {
    this.mDataManager = DataManager.getInstance();
    mCompositeSubscription = new CompositeSubscription();
  }

  @Override public void attachView(MainMvpView mvpView) {
    super.attachView(mvpView);
        /*Observable<?> syncRequest = mDataManager.getCachedRequest(sRequestTag);
        if (syncRequest != null) {
            mCompositeSubscription.add(syncRequest
                    .doOnSubscribe(() -> getMvpView().showSyncInProgress())
                    .subscribe(aVoid -> {}, e -> {
                        getMvpView().endSync(false);
                        Timber.e(e, e.getClass().getCanonicalName());
                        mDataManager.deleteCachedRequest(sRequestTag);
                    }, () -> {
                        getMvpView().endSync(true);
                        mDataManager.deleteCachedRequest(sRequestTag);
                    }));
        }*/
  }

  @Override public void detachView() {
    super.detachView();
    if (mCompositeSubscription.hasSubscriptions()) mCompositeSubscription.unsubscribe();
  }

    /*public void syncData() {
        Observable<Void> syncRequest = mDataManager.syncData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .cache();
        mDataManager.addCachedRequest(syncRequest, sRequestTag);
        mCompositeSubscription.add(syncRequest
                .doOnSubscribe(() -> getMvpView().showSyncInProgress())
                .subscribe(aVoid -> {}, e -> {
                    getMvpView().endSync(false);
                    Timber.e(e, e.getClass().getCanonicalName());
                    mDataManager.deleteCachedRequest(sRequestTag);
                }, () -> {
                    getMvpView().endSync(true);
                    mDataManager.deleteCachedRequest(sRequestTag);
                }));
    }*/
}
