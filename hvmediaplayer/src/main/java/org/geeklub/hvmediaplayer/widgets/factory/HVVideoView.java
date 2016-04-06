package org.geeklub.hvmediaplayer.widgets.factory;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.widget.VideoView;

/**
 * Created by HelloVass on 16/3/24.
 */
public class HVVideoView extends VideoView implements HVPlayable {

  private static final String TAG = HVVideoView.class.getSimpleName();

  private UpdatePlayableTimer mUpdatePlayableTimer;

  private Callback mCallback;

  public HVVideoView(Context context) {
    super(context);
    init();
  }

  @Override public void resetPlayableTimer() {
    mUpdatePlayableTimer = new UpdatePlayableTimer(getDuration(), 250L);
    mUpdatePlayableTimer.start();
  }

  @Override public void stopPlayableTimer() {
    mUpdatePlayableTimer.cancel();
    mUpdatePlayableTimer = null;
  }

  @Override public void pauseHVPlayable() {
    pause();
  }

  @Override public void startHVPlayable() {
    start();
  }

  @Override public boolean isHVPlayablePlaying() {
    return isPlaying();
  }

  @Override public int getHVPlayableBufferPercentage() {
    return getBufferPercentage();
  }

  @Override public int getHVPlayableCurrentPosition() {
    return getCurrentPosition();
  }

  @Override public int getHVPlayableDuration() {
    return getDuration();
  }

  @Override public void seekToHVPlayable(int timeInMillis) {
    seekTo(timeInMillis);
  }

  @Override public void setCallback(Callback callback) {
    mCallback = callback;
  }

  private void init() {

    setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

      @Override public void onPrepared(MediaPlayer mp) {
        if (mCallback != null) {
          mCallback.onPrepared();
        }
      }
    });

    setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mp) {
        if (mCallback != null) {
          mCallback.onCompletion(mp);
        }
      }
    });

    setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mCallback != null) {
          mCallback.onError(mp);
          return true;
        }
        return false;
      }
    });
  }

  private class UpdatePlayableTimer extends CountDownTimer {

    public UpdatePlayableTimer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override public void onTick(long millisUntilFinished) {

      if (mCallback != null && isHVPlayablePlaying()) {
        float percent = (float) getCurrentPosition() / (float) getDuration();
        mCallback.onProgressChanged((int) (percent * 100), getBufferPercentage());
      }
    }

    @Override public void onFinish() {

    }
  }
}
