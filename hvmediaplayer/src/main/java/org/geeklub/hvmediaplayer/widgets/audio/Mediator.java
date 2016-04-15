package org.geeklub.hvmediaplayer.widgets.audio;

import android.media.MediaPlayer;

/**
 * Created by HelloVass on 16/4/13.
 */
public interface Mediator {

  void doPlayPause();

  void updateCurrentTimeWhenDragging(int progress);

  void seekToStopTrackingTouchPosition(int progress);

  void updateCurrentTimeWhenPlaying(int progress, int bufferPercentage);

  void onPrepared(MediaPlayer mp);

  void onCompletion(MediaPlayer mp);

  void onError(MediaPlayer mp, int what, int extra);
}
