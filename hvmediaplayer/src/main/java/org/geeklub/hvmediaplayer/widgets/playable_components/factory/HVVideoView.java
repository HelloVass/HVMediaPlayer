package org.geeklub.hvmediaplayer.widgets.playable_components.factory;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.widget.VideoView;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;

/**
 * Created by HelloVass on 16/3/24.
 */
public class HVVideoView extends VideoView implements IHVPlayable {

  private static final String TAG = HVVideoView.class.getSimpleName();

  private UpdatePlayableTimer mUpdatePlayableTimer;

  private IHVPlayableCallback mIHVPlayableCallback;


  public HVVideoView(Context context) {
    super(context);
    init();
  }

  @Override public void resetUpdatePlayableTimer() {
    mUpdatePlayableTimer = new UpdatePlayableTimer(getDuration(), 250);
    mUpdatePlayableTimer.start();
  }

  @Override public void stopUpdatePlayableTimer() {
    mUpdatePlayableTimer.cancel();
    mUpdatePlayableTimer = null;
  }

  @Override public void pauseIHVPlayable() {
    pause();
  }

  @Override public void startIHVPlayable() {
    start();
  }

  @Override public boolean isIHVPlayablePlaying() {
    return isPlaying();
  }

  @Override public int getIHVPlayableBufferPercentage() {
    return getBufferPercentage();
  }

  @Override public int getIHVPlayableCurrentPosition() {
    return getCurrentPosition();
  }

  @Override public int getIHVPlayableDuration() {
    return getDuration();
  }

  @Override public void IHVPlayableSeekTo(int timeInMillis) {
    seekTo(timeInMillis);
  }

  @Override public void setIHVPlayableOnTouchListener(OnTouchListener onTouchListener) {
    setOnTouchListener(onTouchListener);
  }

  @Override public void setIHVPlayableCallback(IHVPlayableCallback callback) {
    mIHVPlayableCallback = callback;
  }

  private void init() {

    setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

      @Override public void onPrepared(MediaPlayer mp) {
        if (mIHVPlayableCallback != null) {
          mIHVPlayableCallback.onPrepared();
        }
      }
    });

    setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mp) {
        if (mIHVPlayableCallback != null) {
          mIHVPlayableCallback.onCompletion(mp);
        }
      }
    });

    setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mIHVPlayableCallback != null) {
          mIHVPlayableCallback.onError(mp);
          return true;
        }
        return false;
      }
    });
  }

  /**
   * 定时更新“可播放组件”播放进度任务
   */
  private class UpdatePlayableTimer extends CountDownTimer {

    public UpdatePlayableTimer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override public void onTick(long millisUntilFinished) {

      if (mIHVPlayableCallback != null && isIHVPlayablePlaying()) {
        float percent = (float) getCurrentPosition() / (float) getDuration();
        mIHVPlayableCallback.onProgressChanged((int) (percent * 100), getBufferPercentage());
      }
    }

    @Override public void onFinish() {

    }
  }
}
