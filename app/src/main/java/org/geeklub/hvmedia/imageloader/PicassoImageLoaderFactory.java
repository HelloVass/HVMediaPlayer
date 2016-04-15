package org.geeklub.hvmedia.imageloader;

import org.geeklub.hvmediaplayer.imageloader.ImageLoader;
import org.geeklub.hvmediaplayer.imageloader.ImageLoaderFactory;

/**
 * Created by HelloVass on 16/4/14.
 */
public class PicassoImageLoaderFactory implements ImageLoaderFactory {
  @Override public ImageLoader createImageLoader() {
    return new PicassoImageLoader();
  }
}
