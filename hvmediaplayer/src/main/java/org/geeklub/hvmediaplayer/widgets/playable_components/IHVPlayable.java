package org.geeklub.hvmediaplayer.widgets.playable_components;

import android.media.MediaPlayer;
import android.view.View;

/**
 * Created by HelloVass on 16/3/28.
 *
 * 可播放的组件需要实现的接口
 */
public interface IHVPlayable {

  void startIHVPlayable();

  void pauseIHVPlayable();

  int getIHVPlayableDuration();

  int getIHVPlayableCurrentPosition();

  void IHVPlayableSeekTo(int timeInMillis);

  boolean isIHVPlayablePlaying();

  int getIHVPlayableBufferPercentage();

  void resetUpdatePlayableTimer();

  void stopUpdatePlayableTimer();

  void setIHVPlayableOnTouchListener(View.OnTouchListener onTouchListener);

  void setIHVPlayableCallback(IHVPlayableCallback callback);

  interface IHVPlayableCallback {

    void onPrepared();

    void onProgressChanged(int percent, int secondaryProgress);

    void onCompletion(MediaPlayer mediaPlayer);

    void onError(MediaPlayer mediaPlayer);
  }
}
