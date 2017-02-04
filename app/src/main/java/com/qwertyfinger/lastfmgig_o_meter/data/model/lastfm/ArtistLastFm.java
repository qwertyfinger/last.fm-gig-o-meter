package com.qwertyfinger.lastfmgig_o_meter.data.model.lastfm;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;

import com.qwertyfinger.lastfmgig_o_meter.data.model.db.ArtistDb;
import com.qwertyfinger.lastfmgig_o_meter.util.Utils;

//TODO: think about implementation of artist page caching
public class ArtistLastFm {

  private String name;
  private String mbid;

  private SimpleArrayMap<String, String> images;
  //    private Target target;

  private Context context;

  public ArtistLastFm() {
    context = Utils.getAppContext();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMbid() {
    return mbid;
  }

  public void setMbid(String mbid) {
    this.mbid = mbid;
  }

  public SimpleArrayMap<String, String> getImages() {
    return images;
  }

  public void setImages(SimpleArrayMap<String, String> images) {
    this.images = images;
  }

  public String getSmallImage() {
    return images.get("small");
  }

  public String getMediumImage() {
    return images.get("medium");
  }

  public String getLargeImage() {
    return images.get("large");
  }

  public String getExtralargeImage() {
    return images.get("extralarge");
  }

  public String getMegaImage() {
    return images.get("mega");
  }

  public ArtistDb asArtistDb() {
    if (mbid == null || name == null) return null;

    ArtistDb artistDb = new ArtistDb();
    artistDb.setMbid(mbid);
    artistDb.setName(name);
    artistDb.setImageUrl(getExtralargeImage());

    //        TODO: implement screensize-dependent image choosing
        /*if (images != null) {
            artistDb.setImageUrl(getExtralargeImage());
            File image = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), mbid + ".jpg");
            if (!image.exists()) {
                downloadImage(getExtralargeImage())
//                        .retry(3)
                        .doOnError(e -> Timber.e(e, "PicassoException"))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        }*/

    return artistDb;
  }

    /*public ArtistRealm asRealm() {
        ArtistRealm artistRealm = new ArtistRealm();

        if (mbid == null || name == null) return null;

        artistRealm.setMbid(mbid);
        artistRealm.setName(name);
        artistRealm.setPlaycount(playcount);
        artistRealm.setOnTour(onTour);

//        TODO: implement screensize-dependent image choosing
        if (images != null) {
            File image = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), mbid + ".jpg");
            if (!image.exists()) {
                downloadImage(getExtralargeImage())
                        .retry(3)
                        .doOnError(e -> {
                            artistRealm.setImageUrl(getExtralargeImage());
                            Timber.e(e, e.getClass().getCanonicalName());
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe();
            }
        }

        return artistRealm;
    }*/

    /*private Observable<Void> downloadImage(String imageUrl) {
        return Observable.create(subscriber -> {
            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    FileOutputStream out = null;

                    try {
                        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                mbid + ".jpg");
                        out = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        subscriber.onError(e);
                    }

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    subscriber.onCompleted();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    subscriber.onError(new Exception("Could not download image: " + getExtralargeImage()));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            OkHttp3Downloader okHttpDownloader = new OkHttp3Downloader(context);
            new Picasso.Builder(context)
                    .downloader(okHttpDownloader)
                    .build()
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.NO_STORE)
                    .config(Bitmap.Config.RGB_565)
                    .into(target);
        });
    }*/
}
