package org.geeklub.hvmediaplayer.widgets;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import org.geeklub.hvmediaplayer.utils.DensityUtil;

/**
 * Created by HelloVass on 16/3/24.
 */
public class HVMediaPlayer extends RelativeLayout {

  private static final String TAG = HVMediaPlayer.class.getSimpleName();

  private HVVideoView mHVVideoView;

  private HVMediaController mHVMediaController;

  private HVMediaPlayerCallback mHVMediaPlayerCallback;

  private MyHVMediaPlayerImpl mMyHVMediaPlayer = new MyHVMediaPlayerImpl();

  private boolean mIsMediaControllerHidden = false;

  public HVMediaPlayer(Context context) {
    this(context, null);
  }

  public HVMediaPlayer(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HVMediaPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void setHVMediaPlayerCallback(HVMediaPlayerCallback callback) {
    mHVMediaPlayerCallback = callback;
  }

  public void loadAndPlay(String videoUrl) {
    mHVVideoView.setVideoURI(Uri.parse(videoUrl));
  }

  private void init() {

    mHVVideoView = new HVVideoView(getContext());

    LayoutParams videoViewParams =
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    videoViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    mHVVideoView.setLayoutParams(videoViewParams);

    mHVMediaController = new HVMediaController(getContext());
    LayoutParams controllerParams =
        new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 40));
    controllerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    mHVMediaController.setLayoutParams(controllerParams);

    addView(mHVVideoView);
    addView(mHVMediaController);

    setUpCallbacks();
  }

  private void setUpCallbacks() {

    mHVVideoView.setIHVMediaPlayer(mMyHVMediaPlayer);

    mHVVideoView.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          hideOrShowMediaController();
        }

        return false;
      }
    });

    mHVVideoView.setHVVideoCallback(new HVVideoView.HVVideoCallback() {

      @Override public void onProgressChanged(int progress, int secondaryProgress) {
        mHVMediaController.setSeekBarProgress(progress); // 设置 SeekBar 的进度
        mHVMediaController.setSeekBarSecondaryProgress(secondaryProgress); // 设置 SeekBar 的缓冲进度
        mHVMediaController.setCurrentTime(mHVVideoView.getCurrentPosition());// 设置当前时间
      }

      @Override public void onPrepared() {
        mHVMediaController.getState().preparedState(mHVVideoView, mHVMediaController);
      }

      @Override public void onCompletion() {
        mHVMediaController.getState().finishedState(mHVVideoView, mHVMediaController);
      }

      @Override public void onError() {

      }
    });

    mHVMediaController.setIHVMediaPlayer(mMyHVMediaPlayer);

    mHVMediaController.setHVMediaControllerCallback(
        new HVMediaController.HVMediaControllerCallback() {

          /**
           * 用户“手指拖动并且松开后”的回调方法
           * @param progress 当前 SeekBar 的进度
           */
          @Override public void onProgressChanged(int progress) {

            float percent = (float) progress / (float) 100;
            int timeInMillis = (int) (percent * mHVVideoView.getDuration());
            mHVVideoView.seekTo(timeInMillis);
          }

          @Override public void start() {
            mHVMediaController.getState().startState(mHVVideoView, mHVMediaController);
          }

          @Override public void pause() {
            mHVMediaController.getState().pauseState(mHVVideoView, mHVMediaController);
          }

          @Override public void shrink() {
            if (mHVMediaPlayerCallback != null) {
              mHVMediaPlayerCallback.onExitScreen();
            }
          }

          @Override public void expand() {
            if (mHVMediaPlayerCallback != null) {
              mHVMediaPlayerCallback.onEnterFullScreen();
            }
          }
        });
  }

  private void hideOrShowMediaController() {
    if (mIsMediaControllerHidden) {
      mHVMediaController.animate()
          .translationY(0)
          .setInterpolator(new DecelerateInterpolator(2.0F))
          .setDuration(500)
          .start();
    } else {
      mHVMediaController.animate()
          .translationY(mHVMediaController.getHeight())
          .setInterpolator(new AccelerateInterpolator(2.0F))
          .setDuration(500)
          .start();
    }
    mIsMediaControllerHidden = !mIsMediaControllerHidden;
  }

  public interface IHVMediaPlayer {

    boolean isPlaying();

    int getDuration();

    boolean isDraggingSeekBar();
  }

  public interface HVMediaPlayerCallback {

    void onEnterFullScreen();

    void onExitScreen();
  }

  private class MyHVMediaPlayerImpl implements IHVMediaPlayer {

    @Override public boolean isPlaying() {
      return mHVVideoView.isPlaying();
    }

    @Override public int getDuration() {
      return mHVVideoView.getDuration();
    }

    @Override public boolean isDraggingSeekBar() {
      return mHVMediaController.isDraggingSeekBar();
    }
  }
}
