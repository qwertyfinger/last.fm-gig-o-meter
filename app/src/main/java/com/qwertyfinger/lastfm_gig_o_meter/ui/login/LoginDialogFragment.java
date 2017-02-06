/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.ui.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.qwertyfinger.lastfm_gig_o_meter.R;
import com.qwertyfinger.lastfm_gig_o_meter.databinding.FragmentLoginBinding;
import com.qwertyfinger.lastfm_gig_o_meter.util.Utils;

public class LoginDialogFragment extends DialogFragment implements LoginMvpView {

  private FragmentLoginBinding mBinding;
  private LoginPresenter mPresenter;

  public LoginDialogFragment() {}

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    mBinding = FragmentLoginBinding.inflate(inflater, null, false);
    mBinding.username.setOnEditorActionListener((v, actionId, event) -> {
      if (actionId == 0) {
        handleInput();
        return true;
      }
      return false;
    });
    mBinding.username.requestFocus();

    if (savedInstanceState != null) {
      if (savedInstanceState.getInt("errorMessageVisibility") == View.VISIBLE) {
        mBinding.errorMessage.setVisibility(View.VISIBLE);
      }
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(getString(R.string.login_dialog_title));
    builder.setView(mBinding.getRoot());
    builder.setPositiveButton(getString(R.string.sign_in_button_text), null);
    builder.setNegativeButton(getString(R.string.cancel_button_text), null);

    AlertDialog dialog = builder.create();
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    mPresenter = new LoginPresenter();
    mPresenter.attachView(this);
    String username = mPresenter.getUsername();
    mBinding.username.setText(username);
    mBinding.username.selectAll();

    return dialog;
  }

  @Override public void onStart() {
    super.onStart();
    AlertDialog dialog = (AlertDialog) getDialog();
    if (dialog != null) {
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> handleInput());
    }
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    mPresenter.detachView();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putInt("errorMessageVisibility", mBinding.errorMessage.getVisibility());
    super.onSaveInstanceState(outState);
  }

  @Override public void dismissDialog() {
    dismiss();
  }

  @Override public void showWrongUsernameMessage() {
    if (getActivity() != null) {
      if (getActivity().getResources().getConfiguration().orientation
          == Configuration.ORIENTATION_LANDSCAPE) {
        Utils.showShortToast(getContext(), R.string.wrong_username);
      }
    }
    if (mBinding != null && mBinding.errorMessage != null) {
      mBinding.errorMessage.setVisibility(View.VISIBLE);
    }
  }

  @Override public void showNoConnectionToast() {
    Utils.showNoConnectionToast(getContext());
  }

  @Override public void showErrorToast() {
    Utils.showShortToast(getContext(), R.string.error_message);
  }

  public interface LoginDialogListener {
    void onFinishLoginDialog();
  }

  private void sendBackResult() {
    LoginDialogListener listener = (LoginDialogListener) getTargetFragment();
    listener.onFinishLoginDialog();
    dismiss();
  }

  private void handleInput() {
    String username = mBinding.username.getText().toString();
    if (username.length() > 0) {
      mPresenter.handleInput(username).subscribe(result -> sendBackResult());
    }
  }
}
