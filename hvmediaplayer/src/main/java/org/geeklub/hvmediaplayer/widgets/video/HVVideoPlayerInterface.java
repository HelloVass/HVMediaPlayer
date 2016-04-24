package org.geeklub.hvmediaplayer.widgets.video;

/**
 * Created by HelloVass on 16/4/21.
 */
public interface HVVideoPlayerInterface {

  void close();

  interface OnDismissListener {
    void dismiss(HVVideoPlayerInterface videoPlayer);
  }
}
