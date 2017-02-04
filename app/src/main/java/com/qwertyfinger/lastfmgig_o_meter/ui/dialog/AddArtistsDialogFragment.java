package com.qwertyfinger.lastfmgig_o_meter.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.qwertyfinger.lastfmgig_o_meter.R;
import com.qwertyfinger.lastfmgig_o_meter.databinding.FragmentAddArtistsBinding;
import com.qwertyfinger.lastfmgig_o_meter.util.Utils;

public class AddArtistsDialogFragment extends DialogFragment {

  private FragmentAddArtistsBinding binding;

  public AddArtistsDialogFragment() {
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    binding = FragmentAddArtistsBinding.inflate(inflater, null, false);
    binding.numberSelector.setOnEditorActionListener((v, actionId, event) -> {
      if (actionId == EditorInfo.IME_ACTION_GO) {
        sendBackResult();
        return true;
      }
      return false;
    });
    binding.numberSelector.requestFocus();

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setView(binding.getRoot());
    builder.setTitle(getString(R.string.add_artists_dialog_title));
    builder.setPositiveButton(getString(R.string.add_artists_dialog_button), null);
    builder.setNegativeButton(getString(R.string.cancel_button_text), null);

    AlertDialog dialog = builder.create();
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    return dialog;
  }

  @Override public void onStart() {
    super.onStart();
    AlertDialog dialog = (AlertDialog) getDialog();
    if (dialog != null) {
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> sendBackResult());
    }
  }

  public interface AddArtistDialogListener {
    void onFinishAddArtistsDialog(int number);
  }

  private void sendBackResult() {
    if (Utils.checkConnectionToast(getContext())) {
      AddArtistDialogListener listener = (AddArtistDialogListener) getTargetFragment();
      String numberText = binding.numberSelector.getText().toString();
      if (numberText.length() > 0) {
        Integer number = Integer.valueOf(numberText);
        if (number > 0) {
          listener.onFinishAddArtistsDialog(number);
          dismiss();
        }
      }
    }
  }
}
