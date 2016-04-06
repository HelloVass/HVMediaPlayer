package org.geeklub.hvmediaplayer.widgets.factory;

import android.media.MediaPlayer;

/**
 * Created by HelloVass on 16/4/6.
 */
public interface HVPlayable {

  void startHVPlayable();

  void pauseHVPlayable();

  int getHVPlayableDuration();

  int getHVPlayableCurrentPosition();

  void seekToHVPlayable(int timeInMillis);

  boolean isHVPlayablePlaying();

  int getHVPlayableBufferPercentage();

  void resetPlayableTimer();

  void stopPlayableTimer();

  void setCallback(Callback callback);

  interface Callback {

    void onPrepared();

    void onProgressChanged(int percent, int secondaryProgress);

    void onCompletion(MediaPlayer mediaPlayer);

    void onError(MediaPlayer mediaPlayer);
  }
}
