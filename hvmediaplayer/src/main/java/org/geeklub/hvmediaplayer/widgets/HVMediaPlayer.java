package org.geeklub.hvmediaplayer.widgets;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.utils.TimeUtil;
import org.geeklub.hvmediaplayer.widgets.controller.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.playable_components.factory.HVAudioView;
import org.geeklub.hvmediaplayer.widgets.playable_components.factory.HVVideoView;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;
import org.geeklub.hvmediaplayer.widgets.playable_components.factory.HVMediaPlayerFactory;
import org.geeklub.hvmediaplayer.widgets.player_states.HVMediaPlayerContext;
import org.geeklub.hvmediaplayer.widgets.player_states.concrete_state.PreparingState;

/**
 * Created by HelloVass on 16/3/24.
 */
public class HVMediaPlayer extends RelativeLayout {

  private static final String TAG = HVMediaPlayer.class.getSimpleName();

  private IHVPlayable mIHVPlayable; // 可播放的组件

  private HVMediaController mHVMediaController; // 操作栏

  private HVMediaPlayerCallback mHVMediaPlayerCallback;

  private HVMediaPlayerContext mHVMediaPlayerContext;

  private boolean mIsMediaControllerHidden = false; // 默认隐藏“操作栏”

  private int mPlayerStopPosition = 0;

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

  /**
   * 创建视频播放器
   *
   * @param mediaUrl 媒体的地址
   */
  public void buildVideoPlayer(String mediaUrl) {

    removeAllViews();

    HVVideoView videoView = HVMediaPlayerFactory.createHVVideoView(getContext(), mediaUrl);
    addPlayableView(videoView);
    mIHVPlayable = videoView;

    addControllerView();

    setUpCallbacks();
    setUpMediaPlayerState();
  }

  /**
   * 创建音乐播放器
   *
   * @param mediaUrl 媒体的地址
   * @param coverImageLoader 将显示封面的回调接口
   */
  public void buildAudioPlayer(String mediaUrl, String coverUrl,
      HVAudioView.CoverImageLoader coverImageLoader) {

    removeAllViews();

    HVAudioView audioView =
        HVMediaPlayerFactory.createHVAudioView(getContext(), mediaUrl, coverUrl, coverImageLoader);

    addPlayableView(audioView);
    mIHVPlayable = audioView;

    addControllerView();

    setUpCallbacks();
    setUpMediaPlayerState();
  }

  public void onPause() {
    mPlayerStopPosition = mIHVPlayable.getIHVPlayableCurrentPosition();
    mHVMediaPlayerContext.pause();
  }

  public void onResume() {
    mIHVPlayable.IHVPlayableSeekTo(mPlayerStopPosition);
  }

  private void init() {
    setBackgroundColor(Color.BLACK); // 设置播放器背景为“纯黑”
  }

  private void addPlayableView(View component) {
    LayoutParams videoViewLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    videoViewLayoutParams.addRule(CENTER_IN_PARENT);
    videoViewLayoutParams.leftMargin = DensityUtil.dip2px(getContext(), 8);
    videoViewLayoutParams.rightMargin = DensityUtil.dip2px(getContext(), 8);
    component.setLayoutParams(videoViewLayoutParams);
    addView(component); // 将“可播放的组件”添加到“HVMediaPlayer”中
  }

  private void addControllerView() {
    HVMediaController controller = new HVMediaController(getContext());

    LayoutParams controllerLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 40));
    controllerLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
    controller.setLayoutParams(controllerLayoutParams);
    addView(controller); // 将“操作栏”添加到“HVMediaPlayer”中

    mHVMediaController = controller;
  }

  private void setUpMediaPlayerState() {
    mHVMediaPlayerContext = new HVMediaPlayerContext(mIHVPlayable, mHVMediaController);
    mHVMediaPlayerContext.setPlayerState(new PreparingState(mIHVPlayable, mHVMediaController));
    mHVMediaPlayerContext.prepareAsync();
  }

  /**
   * 设置需要监听的事件
   */
  private void setUpCallbacks() {

    mIHVPlayable.setIHVPlayableOnTouchListener(new OnTouchListener() { // 设置“可播放组件”touch事件

      @Override public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          hideOrShowMediaController();
        }

        return false;
      }
    });

    mIHVPlayable.setIHVPlayableCallback(new IHVPlayable.IHVPlayableCallback() { // 设置“可播放组件”内部回调

      @Override public void onProgressChanged(int progress, int secondaryProgress) {

        Log.i(TAG, "onProgressChanged -->>>" + TimeUtil.getTime(
            mIHVPlayable.getIHVPlayableCurrentPosition()));

        if (!mHVMediaController.isDraggingSeekBar()) { // 如果用户没有拖动“SeeKBar”
          mHVMediaController.setSeekBarProgress(progress);
          mHVMediaController.setSeekBarSecondaryProgress(secondaryProgress);
          mHVMediaController.setCurrentTime(mIHVPlayable.getIHVPlayableCurrentPosition());
        }
      }

      @Override public void onPrepared() {
        mHVMediaPlayerContext.onPrepared();
      }

      @Override public void onCompletion(MediaPlayer mediaPlayer) {

        mHVMediaController.setCurrentTime(0);
        mHVMediaController.setSeekBarProgress(0);

        mHVMediaController.showOrHidePauseButton(false);
        mHVMediaController.showOrHidePlayButton(true);

        mIHVPlayable.IHVPlayableSeekTo(0);
        mIHVPlayable.stopUpdatePlayableTimer();
      }

      @Override public void onError(MediaPlayer mediaPlayer) {
        Log.i(TAG, "onError");
      }
    });

    mHVMediaController.setHVMediaControllerCallback(
        new HVMediaController.HVMediaControllerCallback() {

          @Override public void onProgressChanged(int progress) {
            float percent = (float) progress / (float) 100;
            int timeInMillis = (int) (percent * mIHVPlayable.getIHVPlayableDuration());
            mIHVPlayable.IHVPlayableSeekTo(timeInMillis);
          }

          @Override public void start() {
            if (!mIHVPlayable.isIHVPlayablePlaying()) {
              mHVMediaPlayerContext.start();
            }
          }

          @Override public void pause() {
            if (mIHVPlayable.isIHVPlayablePlaying()) {
              mHVMediaPlayerContext.pause();
            }
          }

          @Override public void shrink() {
            if (mHVMediaPlayerCallback != null && mHVMediaController.isEnterFullScreen()) {
              mHVMediaPlayerCallback.onExitScreen();
              mHVMediaController.setIsEnterFullScreen(false);
            }
          }

          @Override public void expand() {
            if (mHVMediaPlayerCallback != null && !mHVMediaController.isEnterFullScreen()) {
              mHVMediaPlayerCallback.onEnterFullScreen();
              mHVMediaController.setIsEnterFullScreen(true);
            }
          }

          @Override public void updateCurrentTimeWhenDragging(int progress) {
            float percent = (float) progress / (float) 100;
            int timeInMillis = (int) (percent * mIHVPlayable.getIHVPlayableDuration());
            mHVMediaController.setCurrentTime(timeInMillis);
          }
        });
  }

  /**
   * 控制“操作栏”的隐藏和显示
   */
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

  /**
   * HVMediaPlayer 的回调接口
   */
  public interface HVMediaPlayerCallback {

    /**
     * 进入全屏
     */
    void onEnterFullScreen();

    /**
     * 退出全屏
     */
    void onExitScreen();
  }
}
