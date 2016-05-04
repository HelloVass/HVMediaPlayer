package org.geeklub.hvmediaplayer.widgets.audio;

/**
 * Created by HelloVass on 16/4/24.
 */
public interface HVAudioPlayerInterface {

  void close();

  interface OnDismissListener {
    void dismiss(HVAudioPlayerInterface audioPlayer);
  }
}
