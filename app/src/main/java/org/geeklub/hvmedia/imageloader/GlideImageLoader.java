package org.geeklub.hvmedia.imageloader;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.io.File;
import org.geeklub.hvmediaplayer.imageloader.ImageLoader;

/**
 * Created by HelloVass on 16/4/14.
 */
public class GlideImageLoader implements ImageLoader {

  private Context mContext;

  public GlideImageLoader(Context context) {
    mContext = context;
  }

  @Override public void displayImage(ImageView targetImageView, String imageUrl) {
    Glide.with(mContext).load(imageUrl).centerCrop().into(targetImageView);
  }

  @Override public void displayImage(ImageView targetImageView, File imageFilePath) {

  }
}
