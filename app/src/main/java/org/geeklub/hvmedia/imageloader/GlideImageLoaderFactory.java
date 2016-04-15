package org.geeklub.hvmedia.imageloader;

import android.content.Context;
import org.geeklub.hvmediaplayer.imageloader.ImageLoader;
import org.geeklub.hvmediaplayer.imageloader.ImageLoaderFactory;

/**
 * Created by HelloVass on 16/4/14.
 */
public class GlideImageLoaderFactory implements ImageLoaderFactory {

  private Context mContext;

  public GlideImageLoaderFactory(Context context) {
    mContext = context;
  }

  @Override public ImageLoader createImageLoader() {
    return new GlideImageLoader(mContext);
  }
}
