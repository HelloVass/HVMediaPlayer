package org.geeklub.hvmediaplayer.widgets;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.VideoView;
import org.geeklub.hvmediaplayer.utils.DensityUtil;

/**
 * Created by HelloVass on 16/3/24.
 */
public class HVVideoView extends VideoView {

  private static final String TAG = HVVideoView.class.getSimpleName();

  private static final int DEFAULT_VIDEO_VIEW_WIDTH = 160;

  private static final int DEFAULT_VIDEO_VIEW_HEIGHT = 90;

  private UpdateMediaControllerTimer mUpdateMediaControllerTimer;

  private HVVideoCallback mHVVideoCallback;

  private HVMediaPlayer.IHVMediaPlayer mIHVMediaPlayer;

  public HVVideoView(Context context) {
    this(context, null);
  }

  public HVVideoView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HVVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = getDefaultSize(DensityUtil.dip2px(getContext(), DEFAULT_VIDEO_VIEW_WIDTH),
        widthMeasureSpec);
    int height = getDefaultSize(DensityUtil.dip2px(getContext(), DEFAULT_VIDEO_VIEW_HEIGHT),
        heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

  public void setHVVideoCallback(HVVideoCallback HVVideoCallback) {
    mHVVideoCallback = HVVideoCallback;
  }

  public void setIHVMediaPlayer(HVMediaPlayer.IHVMediaPlayer IHVMediaPlayer) {
    mIHVMediaPlayer = IHVMediaPlayer;
  }

  public void resetUpdateMediaControllerTimer() {
    mUpdateMediaControllerTimer = new UpdateMediaControllerTimer(getDuration(), 250);
    mUpdateMediaControllerTimer.start();
  }

  public void stopUpdateMediaControllerTimer() {
    mUpdateMediaControllerTimer.cancel();
    mUpdateMediaControllerTimer = null;
  }

  private void init() {

    setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

      @Override public void onPrepared(MediaPlayer mp) {

        if (mHVVideoCallback != null) {
          mHVVideoCallback.onPrepared();
        }
      }
    });

    setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mp) {
        if (mHVVideoCallback != null) {
          mHVVideoCallback.onCompletion();
        }
      }
    });

    setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override public boolean onError(MediaPlayer mp, int what, int extra) {

        if (mHVVideoCallback != null) {
          mHVVideoCallback.onError();
        }

        return false;
      }
    });
  }

  private class UpdateMediaControllerTimer extends CountDownTimer {

    public UpdateMediaControllerTimer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override public void onTick(long millisUntilFinished) {

      if (mHVVideoCallback != null && !mIHVMediaPlayer.isDraggingSeekBar()) {
        float percent = (float) getCurrentPosition() / (float) getDuration();
        mHVVideoCallback.onProgressChanged((int) (percent * 100), getBufferPercentage());
      }
    }

    @Override public void onFinish() {

    }
  }

  public interface HVVideoCallback {

    void onPrepared();

    void onCompletion();

    void onError();

    /**
     * @param percent 当前播放进度百分比
     * @param secondaryProgress 缓冲进度
     */
    void onProgressChanged(int percent, int secondaryProgress);
  }
}
