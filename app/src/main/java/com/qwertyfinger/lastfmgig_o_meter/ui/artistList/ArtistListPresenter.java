package com.qwertyfinger.lastfmgig_o_meter.ui.artistlist;

import com.qwertyfinger.lastfmgig_o_meter.data.DataManager;
import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;
import com.qwertyfinger.lastfmgig_o_meter.ui.base.BasePresenter;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class ArtistListPresenter extends BasePresenter<ArtistsListMvpView> {

    private final DataManager mDataManager;
    private CompositeSubscription mCompositeSubscription;

    public ArtistListPresenter() {
        this.mDataManager = DataManager.getInstance();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(ArtistsListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mCompositeSubscription.hasSubscriptions()) mCompositeSubscription.unsubscribe();
    }

    public void setArtistsLimit(int limit) {
        mDataManager.setArtistsLimit(limit);
    }

    public String getUsername() {
        return mDataManager.getUsername();
    }

    public int getArtistsLimit() {
        return mDataManager.getArtistsLimit();
    }

    public void syncData() {
        mCompositeSubscription.add(mDataManager.syncData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> getMvpView().showSyncInProgress())
                .subscribe(aVoid -> {}, e -> {
                    getMvpView().endSync(false);
                    Timber.e(e, e.getClass().getCanonicalName());
                }, () -> {
                    displayRankings();
                    getMvpView().endSync(true);
                }));
    }

    public void syncArtists(List<ArtistDb> artists) {
        mCompositeSubscription.add(mDataManager.syncArtists(artists)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> getMvpView().showSyncInProgress())
                .subscribe(aVoid -> {}, e -> {
                    getMvpView().endSync(false);
                    Timber.e(e, e.getClass().getCanonicalName());
                }, () -> {
                    displayRankings();
                    getMvpView().endSync(true);
                }));
    }

    public void displayRankings() {
        mCompositeSubscription.add(mDataManager.getAllArtists()
                .retry(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(artists -> {
                    getMvpView().displayRankings(artists);
                }, e -> Timber.e(e, e.getClass().getCanonicalName())));
    }

    public void deleteArtist(String name) {
        mCompositeSubscription.add(mDataManager.deleteArtist(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Timber.e(e, e.getClass().getCanonicalName()))
                .subscribe(aVoid -> {}, e -> getMvpView().showErrorMessage()));
    }

    public Observable<Void> clearData() {
        return Observable.create(subscriber -> {
            mCompositeSubscription.add(mDataManager.clearData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnError(e -> Timber.e(e, e.getClass().getCanonicalName()))
                    .subscribe(aVoid -> {}, e -> {
                        getMvpView().showErrorMessage();
                        subscriber.onError(e);
                    }, () -> {
                        mDataManager.setArtistsLimit(50);
                        displayRankings();
                        subscriber.onCompleted();
                    }));
        });
    }
}
