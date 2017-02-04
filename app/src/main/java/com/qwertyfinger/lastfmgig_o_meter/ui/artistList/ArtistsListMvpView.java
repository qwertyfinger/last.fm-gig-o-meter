package com.qwertyfinger.lastfmgig_o_meter.ui.artistlist;

import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;
import com.qwertyfinger.lastfmgig_o_meter.ui.base.MvpView;

import java.util.List;

public interface ArtistsListMvpView extends MvpView {
  void displayRankings(List<ArtistDb> artists);

  void showSyncInProgress();

  void endSync(boolean isSuccessful);

  void showErrorMessage();

  void showProgressBar();

  void hideProgressBar();
}
