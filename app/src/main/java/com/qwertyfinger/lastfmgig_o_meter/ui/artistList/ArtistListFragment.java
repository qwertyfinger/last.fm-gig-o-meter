package com.qwertyfinger.lastfmgig_o_meter.ui.artistlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.qwertyfinger.lastfmgig_o_meter.BuildConfig;
import com.qwertyfinger.lastfmgig_o_meter.LastFmGigometerApp;
import com.qwertyfinger.lastfmgig_o_meter.R;
import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;
import com.qwertyfinger.lastfmgig_o_meter.databinding.FragmentListBinding;
import com.qwertyfinger.lastfmgig_o_meter.ui.dialog.AddArtistsDialogFragment;
import com.qwertyfinger.lastfmgig_o_meter.ui.dialog.EraseDialogFragment;
import com.qwertyfinger.lastfmgig_o_meter.ui.listener.ListScrollListener;
import com.qwertyfinger.lastfmgig_o_meter.ui.login.LoginDialogFragment;
import com.qwertyfinger.lastfmgig_o_meter.util.Constants;
import com.qwertyfinger.lastfmgig_o_meter.util.Utils;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import static com.qwertyfinger.lastfmgig_o_meter.ui.dialog.AddArtistsDialogFragment.AddArtistDialogListener;
import static com.qwertyfinger.lastfmgig_o_meter.ui.dialog.EraseDialogFragment.EraseDialogListener;
import static com.qwertyfinger.lastfmgig_o_meter.ui.login.LoginDialogFragment.LoginDialogListener;

public class ArtistListFragment extends android.support.v4.app.Fragment
    implements ArtistsListMvpView, LoginDialogListener, EraseDialogListener,
    AddArtistDialogListener {

  private ArtistListPresenter mPresenter;
  private List<ArtistDb> mArtists;
  private ListView mListView;
  private Parcelable mListState;
  private FragmentListBinding mBinding;

  private MenuItem mAddArtistsItem;
  private MenuItem mSyncItem;
  private ImageView mSyncAnimation;
  private Animation mRotation;
  private boolean mSyncInProgress;
  private boolean mEraseInProgress;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    setHasOptionsMenu(true);
    mPresenter = new ArtistListPresenter();
    mPresenter.attachView(this);
    if (mPresenter.getUsername().isEmpty()) {
      showDialogFragment(new LoginDialogFragment(), "loginFragment");
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mBinding = FragmentListBinding.inflate(inflater, container, false);
    mListView = mBinding.artistList;
    mListView.setOnScrollListener(new ListScrollListener(getContext()));

    if (savedInstanceState != null) {
      if (savedInstanceState.getInt("progressBarVisibility") == View.VISIBLE) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
      }
    }

    //        TODO: check unwanted side effects
    if (mArtists == null) {
      mPresenter.displayRankings();
    } else {
      displayRankings(mArtists);
    }

    return mBinding.getRoot();
  }

  @Override public void onStop() {
    super.onStop();
    if (mListView != null) mListState = mListView.onSaveInstanceState();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mPresenter.detachView();
    if (BuildConfig.DEBUG) {
      RefWatcher refWather = LastFmGigometerApp.getRefWatcher(getActivity());
      refWather.watch(this);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mAddArtistsItem = null;
    mSyncItem = null;
    mSyncAnimation = null;
    mRotation = null;
    mBinding = null;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putInt("progressBarVisibility", mBinding.progressBar.getVisibility());
    super.onSaveInstanceState(outState);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    mSyncItem = menu.findItem(R.id.sync_item);
    mAddArtistsItem = menu.findItem(R.id.add_artists_to_sync_item);
        /*if (mArtists != null && mPresenter.getArtistsLimit() == mArtists.size()) {
            mAddArtistsItem.setVisible(false);
        }*/
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    if (mSyncInProgress) startSyncAnimation();
    updateAddButtonVisibility();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

      case R.id.sync_item:
        if (!mEraseInProgress) {
          if (!mSyncInProgress) {
            if (Utils.checkConnectionSnackbar(mBinding.getRoot())) {
              if (!mPresenter.getUsername().isEmpty()) {
                mPresenter.syncData();
              } else {
                Utils.showShortSnackbar(getView(), R.string.no_user_message);
              }
            }
          } else {
            showWaitUntilSyncSnackbar();
          }
        } else {
          showWaitUntilEraseSnackbar();
        }
        return true;

      case R.id.log_into_lastfm_item:
        if (!mEraseInProgress) {
          if (!mSyncInProgress) {
            showDialogFragment(new LoginDialogFragment(), "loginFragment");
          } else {
            showWaitUntilSyncSnackbar();
          }
        } else {
          showWaitUntilEraseSnackbar();
        }
        return true;

      //            case R.id.sort_item:
      //
      //                return true;

      case R.id.clear_data_item:
        if (!mEraseInProgress) {
          if (mSyncInProgress) {
            showWaitUntilSyncSnackbar();
          } else if (mArtists == null || mArtists.isEmpty() || mPresenter.getUsername().isEmpty()) {
            Utils.showShortSnackbar(getView(), R.string.no_data_message);
          } else {
            showDialogFragment(new EraseDialogFragment(), "eraseDialogFragment");
          }
        } else {
          showWaitUntilEraseSnackbar();
        }
        return true;

      case R.id.add_artists_to_sync_item:
        if (!mEraseInProgress) {
          if (!mSyncInProgress) {
            if (Utils.checkConnectionSnackbar(getView())) {
              if (mArtists != null && !mArtists.isEmpty()) {
                showDialogFragment(new AddArtistsDialogFragment(), "addArtistsFragment");
              } else if (!mPresenter.getUsername().isEmpty()) {
                Utils.showShortSnackbar(getView(), R.string.no_artists_to_add_message);
              } else {
                Utils.showShortSnackbar(getView(), R.string.no_user_message);
              }
            }
          } else {
            showWaitUntilSyncSnackbar();
          }
        } else {
          showWaitUntilEraseSnackbar();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onFinishLoginDialog() {
    mPresenter.clearData().doOnSubscribe(() -> mEraseInProgress = true).doOnCompleted(() -> {
      mEraseInProgress = false;
      mPresenter.syncData();
    }).subscribe();
  }

  @Override public void onFinishEraseDialog() {
    mPresenter.clearData().doOnSubscribe(() -> mEraseInProgress = true).doOnCompleted(() -> {
      mEraseInProgress = false;
    }).subscribe();
  }

  @Override public void onFinishAddArtistsDialog(int number) {
    int firstNonSyncedArtistIndex = 0;
    for (int i = 0; i < mArtists.size(); i++) {
      ArtistDb artist = mArtists.get(i);
      if (artist.getSyncStatus() == Constants.SYNC_STATUS_NOT_SELECTED) {
        firstNonSyncedArtistIndex = i;
        break;
      }
    }

    int end = firstNonSyncedArtistIndex + number;
    if (end > mArtists.size() - 1) {
      end = mArtists.size();
      mAddArtistsItem.setVisible(false);
    }
    mPresenter.setArtistsLimit(end);
    if (Utils.checkConnectionSnackbar(getView())) {
      mPresenter.syncArtists(mArtists.subList(firstNonSyncedArtistIndex, end));
    }
  }

  @Override public void displayRankings(List<ArtistDb> artists) {
    if (mArtists == null) mArtists = artists;
    if (mListView.getAdapter() == null) {
      mListView.setAdapter(new ArtistListAdapter(artists));
      //            TODO: investigate possible wrong call
      if (mListState != null) mListView.onRestoreInstanceState(mListState);
    } else {
      mArtists.clear();
      mArtists.addAll(artists);
      ((ArtistListAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }
    updateAddButtonVisibility();
    checkListEmpty();
  }

  @Override public void showSyncInProgress() {
    mSyncInProgress = true;
    showProgressBar();
    startSyncAnimation();
  }

  @Override public void endSync(boolean isSuccessful) {
    mSyncInProgress = false;
    hideProgressBar();
    if (mSyncItem != null && mSyncItem.getActionView() != null) {
      mSyncItem.getActionView().clearAnimation();
      mSyncItem.setActionView(null);
    }
    if (mBinding != null) {
      Utils.showLongSnackbar(mBinding.getRoot(),
          isSuccessful ? R.string.sync_finished : R.string.sync_failed);
    }
  }

  @Override public void showErrorMessage() {
    if (mBinding != null) {
      Utils.showShortSnackbar(getView(), R.string.operation_failed);
    }
  }

  @Override public void showProgressBar() {
    if (mBinding != null && mBinding.progressBar != null) {
      mBinding.progressBar.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideProgressBar() {
    if (mBinding != null && mBinding.progressBar != null) {
      mBinding.progressBar.setVisibility(View.INVISIBLE);
    }
  }

  @SuppressLint("InflateParams") private void startSyncAnimation() {
    if (mSyncItem != null) {
      if (mSyncAnimation == null) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mSyncAnimation = (ImageView) inflater.inflate(R.layout.sync_item, null);
      }

      if (mRotation == null) {
        mRotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_sync);
        mRotation.setRepeatCount(Animation.INFINITE);
      }

      mSyncAnimation.startAnimation(mRotation);
      mSyncItem.setActionView(mSyncAnimation);
    }
  }

  private void checkListEmpty() {
    if (mArtists == null || mArtists.isEmpty()) {
      if (mPresenter.getUsername().isEmpty()) {
        setEmptyNoUser();
      } else {
        setEmptyNoArtists();
      }
    }
  }

  private void setEmptyNoUser() {
    mBinding.noUser.setVisibility(View.VISIBLE);
    mBinding.noArtists.setVisibility(View.INVISIBLE);
    if (mBinding != null && mBinding.artistList != null && mBinding.noUser != null) {
      mBinding.artistList.setEmptyView(mBinding.noUser);
    }
  }

  private void setEmptyNoArtists() {
    mBinding.noArtists.setVisibility(View.VISIBLE);
    mBinding.noUser.setVisibility(View.INVISIBLE);
    if (mBinding != null && mBinding.artistList != null && mBinding.noArtists != null) {
      mBinding.artistList.setEmptyView(mBinding.noArtists);
    }
  }

  private void updateAddButtonVisibility() {
    if (mArtists != null) {
      if (mPresenter.getArtistsLimit() < mArtists.size()) {
        mAddArtistsItem.setVisible(true);
      } else {
        mAddArtistsItem.setVisible(false);
      }
    }
  }

  private void showWaitUntilSyncSnackbar() {
    Utils.showShortSnackbar(getView(), R.string.wait_until_sync_finish);
  }

  private void showWaitUntilEraseSnackbar() {
    Utils.showShortSnackbar(getView(), R.string.wait_until_erase_finish);
  }

  private void showDialogFragment(DialogFragment dialogFragment, String tag) {
    FragmentManager fragmentManager = getFragmentManager();
    dialogFragment.setTargetFragment(ArtistListFragment.this, 0);
    dialogFragment.show(fragmentManager, tag);
  }
}
