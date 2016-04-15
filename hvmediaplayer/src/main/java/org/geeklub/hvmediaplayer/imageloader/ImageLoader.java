package org.geeklub.hvmediaplayer.imageloader;

import android.widget.ImageView;
import java.io.File;

/**
 * Created by HelloVass on 16/4/14.
 */
public interface ImageLoader {

  void displayImage(ImageView targetImageView, String imageUrl);

  void displayImage(ImageView targetImageView, File imageFilePath);
}
