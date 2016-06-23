package com.qwertyfinger.lastfmgig_o_meter.ui.artistlist;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qwertyfinger.lastfmgig_o_meter.R;
import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;
import com.qwertyfinger.lastfmgig_o_meter.databinding.ItemArtistBinding;
import com.qwertyfinger.lastfmgig_o_meter.util.Constants;
import com.qwertyfinger.lastfmgig_o_meter.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ArtistListAdapter extends BaseAdapter {

    private List<ArtistDb> mArtists;
//    private final WeakReferenceOnListChangedCallback callback = new WeakReferenceOnListChangedCallback(this);

    public ArtistListAdapter(List<ArtistDb> artists) {
        mArtists = artists;
        notifyDataSetChanged();
    }

    /*public void setItems(List<ArtistDb> artists) {
        if (mArtists == artists) {
            return;
        }
        if (mArtists instanceof ObservableList) {
            ((ObservableList<ArtistDb>) mArtists).removeOnListChangedCallback(callback);
        }
        if (artists instanceof ObservableList) {
            ((ObservableList<ArtistDb>) artists).addOnListChangedCallback(callback);
        }
        mArtists = artists;
        notifyDataSetChanged();
    }*/

    @Override
    public int getCount() {
        return mArtists.size();
    }

    @Override
    public ArtistDb getItem(int position) {
        return mArtists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemArtistBinding binding;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = ItemArtistBinding.inflate(inflater, parent, false);
            binding.getRoot().setTag(binding);
        } else {
            binding = (ItemArtistBinding) convertView.getTag();
        }

        binding.setArtist(getItem(position));
        binding.executePendingBindings();

        return binding.getRoot();
    }

    @BindingAdapter({"bind:imageUrl", "bind:error"})
    public static void loadImage(ImageView imageView, String url, Drawable error) {
        try {
            Picasso.with(imageView.getContext())
                    .load(url)
                    .error(error)
                    .config(Bitmap.Config.RGB_565)
                    .resizeDimen(R.dimen.artist_image_size, R.dimen.artist_image_size)
                    .centerCrop()
                    .into(imageView);
        } catch (IllegalArgumentException e) {
            Picasso.with(imageView.getContext())
                    .load(R.drawable.no_artist_image)
                    .config(Bitmap.Config.RGB_565)
                    .resizeDimen(R.dimen.artist_image_size, R.dimen.artist_image_size)
                    .centerCrop()
                    .into(imageView);
        }
    }

    @BindingAdapter({"bind:score", "bind:syncStatus"})
    public static void setScore(ProgressBar circleBar, double artistScore, int syncStatus) {
        if (syncStatus == Constants.SYNC_STATUS_HAS_SCORE) {
            circleBar.setProgress(artistScore > 100 ? 100 : (int) Math.round(artistScore));
            circleBar.getProgressDrawable()
                    .setColorFilter(Utils.getScoreColor(artistScore), PorterDuff.Mode.SRC_ATOP);

            if (artistScore >= 100) {
                Animation blink = new AlphaAnimation(1.0f, 0.7f);
                blink.setDuration(750);
                blink.setRepeatMode(Animation.REVERSE);
                blink.setRepeatCount(Animation.INFINITE);
                circleBar.startAnimation(blink);
            } else {
                circleBar.clearAnimation();
            }
        } else {
            circleBar.clearAnimation();
            circleBar.setProgress(0);
        }
    }

    @BindingAdapter({"bind:score", "bind:syncStatus"})
    public static void setScore(TextView textView, double artistScore, int syncStatus) {
        textView.setTextColor(Utils.getScoreColor(artistScore));
        if (syncStatus == Constants.SYNC_STATUS_HAS_SCORE) {
            textView.setText(String.format(Locale.getDefault(), "%d%%",
                    artistScore > 100 ? 100 : (int) Math.round(artistScore)));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else if (syncStatus == Constants.SYNC_STATUS_NO_INFO) {
            textView.setText("—");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        } else if (syncStatus == Constants.SYNC_STATUS_FAILED) {
            textView.setText("!");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            textView.setTextColor(Color.parseColor("red"));
        } else textView.setText(null);
    }

    /*private static class WeakReferenceOnListChangedCallback extends ObservableList.OnListChangedCallback<ObservableList<ArtistDb>> {
        final WeakReference<ArtistListAdapter> adapterRef;

        WeakReferenceOnListChangedCallback(ArtistListAdapter adapter) {
            this.adapterRef = new WeakReference<>(adapter);
        }

        @Override
        public void onChanged(ObservableList sender) {
            ArtistListAdapter adapter = adapterRef.get();
            if (adapter == null) {
                return;
            }
            ensureChangeOnMainThread();
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            onChanged(sender);
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            onChanged(sender);
        }
    }

    *//**
     * Ensures the call was made on the main thread. This is enforced for all ObservableList change
     * operations.
     *//*
    private static void ensureChangeOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("You must only modify the ObservableList on the main thread.");
        }
    }*/

}
