/*
 * Copyright (c) 2017 Andriy Chubko
 */

package com.qwertyfinger.lastfm_gig_o_meter.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.qwertyfinger.lastfm_gig_o_meter.R;

public class EraseDialogFragment extends DialogFragment {

  public EraseDialogFragment() {}

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle(getString(R.string.erase_confirmation_dialog_title));
    builder.setMessage(getString(R.string.erase_confirmation_dialog_message));
    builder.setPositiveButton(getString(R.string.erase_confirmation_dialog_button),
        (dialog, which) -> sendBackResult());
    builder.setNegativeButton(getString(R.string.cancel_button_text), null);

    return builder.show();
  }

  public interface EraseDialogListener {
    void onFinishEraseDialog();
  }

  private void sendBackResult() {
    EraseDialogListener listener = (EraseDialogListener) getTargetFragment();
    listener.onFinishEraseDialog();
  }
}
