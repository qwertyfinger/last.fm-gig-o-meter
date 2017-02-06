/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.qwertyfinger.lastfm_gig_o_meter.R;
import com.qwertyfinger.lastfm_gig_o_meter.ui.artistlist.ArtistListFragment;
import com.qwertyfinger.lastfm_gig_o_meter.util.leak.IMMLeaks;

public class MainActivity extends AppCompatActivity implements MainMvpView {

  //    private MainPresenter mPresenter;
  //    private MenuItem mSyncItem;
  //    private ImageView mSyncAnimation;
  //    private Animation mRotation;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    IMMLeaks.fixFocusedViewLeak(getApplication());

    //        mPresenter = new MainPresenter();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ArtistListFragment mListFragment;
    if (savedInstanceState != null) {
      mListFragment =
          (ArtistListFragment) getSupportFragmentManager().findFragmentByTag("listFragment");
    } else {
      mListFragment = new ArtistListFragment();
    }
    showFragment(mListFragment, "listFragment");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //        mPresenter.detachView();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    //        mSyncItem = menu.findItem(R.id.sync_item);
    //        mPresenter.attachView(this);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      //            case R.id.about_item:
      //
      //                return true;
      //            case R.id.sync_item:
      //                mPresenter.syncData();
      //                return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

    /*@Override
    public void showSyncInProgress() {
        startSyncAnimation();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void endSync(boolean isSuccessful) {
        if (mSyncItem != null && mSyncItem.getActionView() != null) {
            mSyncItem.getActionView().clearAnimation();
            mSyncItem.setActionView(null);
        }
        Snackbar.make(findViewById(R.id.rootLinearLayout),
                isSuccessful ? R.string.sync_finished : R.string.sync_failed, Snackbar.LENGTH_LONG).show();
    }*/

  @Override public void showFragment(Fragment fragment, String tag) {
    if (!fragment.isInLayout()) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.placeholder, fragment, tag)
          .commit();
    }
  }

    /*@SuppressLint("InflateParams")
    private void startSyncAnimation() {
        if (mSyncItem != null) {
            if (mSyncAnimation == null) {
                LayoutInflater inflater = LayoutInflater.from(this);
                mSyncAnimation = (ImageView) inflater.inflate(R.layout.sync_item, null);
            }

            if (mRotation == null) {
                mRotation = AnimationUtils.loadAnimation(this, R.anim.rotate_sync);
                mRotation.setRepeatCount(Animation.INFINITE);
            }

            mSyncAnimation.startAnimation(mRotation);
            mSyncItem.setActionView(mSyncAnimation);
        }
    }*/

   /* public MainPresenter getMainPresenter() {
        return mPresenter;
    }*/
}
