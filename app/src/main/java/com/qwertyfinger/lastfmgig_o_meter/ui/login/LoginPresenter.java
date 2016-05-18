package com.qwertyfinger.lastfmgig_o_meter.ui.login;

import com.qwertyfinger.lastfmgig_o_meter.data.DataManager;
import com.qwertyfinger.lastfmgig_o_meter.ui.base.BasePresenter;
import com.qwertyfinger.lastfmgig_o_meter.util.Utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private final DataManager mDataManager;
    private CompositeSubscription mCompositeSubscription;


    public LoginPresenter() {
        this.mDataManager = DataManager.getInstance();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
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
                                subscriber.onNext(true);
                            }
                            else if (getMvpView() != null) getMvpView().showWrongUsernameMessage();
                        }, e -> {
                            if (getMvpView() != null) getMvpView().showErrorToast();
                        }, subscriber::onCompleted);
            } else {
                if (getMvpView() != null) getMvpView().showNoConnectionSnackbar();
                subscriber.onCompleted();
            }
        });
    }

    public String getUsername() {
        return mDataManager.getUsername();
    }
}
