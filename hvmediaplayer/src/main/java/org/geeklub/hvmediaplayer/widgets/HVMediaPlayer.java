package org.geeklub.hvmediaplayer.widgets;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.factory.HVAudioController;
import org.geeklub.hvmediaplayer.widgets.factory.HVAudioPlayerFactory;
import org.geeklub.hvmediaplayer.widgets.factory.HVAudioView;
import org.geeklub.hvmediaplayer.widgets.factory.HVController;
import org.geeklub.hvmediaplayer.widgets.factory.HVPlayable;
import org.geeklub.hvmediaplayer.widgets.factory.HVVideoController;
import org.geeklub.hvmediaplayer.widgets.factory.HVVideoPlayerFactory;
import org.geeklub.hvmediaplayer.widgets.factory.HVVideoView;
import org.geeklub.hvmediaplayer.widgets.states.HVMediaPlayerContext;

/**
 * Created by HelloVass on 16/3/24.
 */
public class HVMediaPlayer extends RelativeLayout {

  private static final String TAG = HVMediaPlayer.class.getSimpleName();

  private HVPlayable mHVPlayable; // 可播放组件

  private HVController mHVController; // 操作栏

  private HVMediaPlayerContext mHVMediaPlayerContext;

  private Callback mCallback;

  private boolean mIsMediaControllerHidden = false; // 默认隐藏“操作栏”

  private int mPlayerStopPosition = 0; // 记录停止播放的位置

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

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public void onPause() {
    mPlayerStopPosition = mHVPlayable.getHVPlayableCurrentPosition();
    mHVMediaPlayerContext.setPlayerState(mHVMediaPlayerContext.getPausedState());
    mHVMediaPlayerContext.pause();
  }

  public void onResume() {
    mHVPlayable.seekToHVPlayable(mPlayerStopPosition);
  }

  /**
   * 创建音乐播放器
   *
   * @param audioUrl 媒体的地址
   * @param coverImageLoader 将显示封面的回调接口
   */
  public void buildAudioPlayer(String audioUrl, String coverUrl,
      HVAudioView.CoverImageLoader coverImageLoader) {

    removeAllViews();

    HVAudioPlayerFactory audioPlayerFactory = new HVAudioPlayerFactory(getContext());

    final HVAudioView audioView = (HVAudioView) audioPlayerFactory.createPlayable();
    audioView.setAudioURI(Uri.parse(audioUrl));
    audioView.setCoverUrl(coverUrl);
    audioView.setCoverImageLoader(coverImageLoader);
    addPlayable(audioView);

    final HVAudioController audioController =
        (HVAudioController) audioPlayerFactory.createController();
    addController(audioController);

    audioView.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          hideOrShowMediaController(audioController);
        }
        return false;
      }
    });

    audioView.setCallback(new HVPlayable.Callback() {

      @Override public void onPrepared() {
        mHVMediaPlayerContext.onPrepared();
      }

      @Override public void onProgressChanged(int percent, int secondaryProgress) {

        if (!audioController.isDraggingSeekBar()) {
          audioController.setSeekBarProgress(percent);
          audioController.setSeekBarSecondaryProgress(secondaryProgress);
          audioController.setCurrentTime(audioView.getHVPlayableCurrentPosition());
        }
      }

      @Override public void onCompletion(MediaPlayer mediaPlayer) {

        audioController.setCurrentTime(0);
        audioController.setSeekBarProgress(0);

        audioController.hidePauseButton();
        audioController.showPlayButton();

        audioView.seekToHVPlayable(0);
        audioView.stopPlayableTimer();
      }

      @Override public void onError(MediaPlayer mediaPlayer) {

      }
    });

    audioController.setCallback(new HVAudioController.Callback() {

      @Override public void start() {

        if (!audioView.isHVPlayablePlaying()) {
          mHVMediaPlayerContext.start();
        }
      }

      @Override public void pause() {

        if (audioView.isHVPlayablePlaying()) {
          mHVMediaPlayerContext.pause();
        }
      }

      @Override public void updateCurrentTimeWhenDragging(int progress) {

        float percent = (float) progress / (float) 100;
        int timeInMillis = (int) (percent * audioView.getHVPlayableDuration());
        audioController.setCurrentTime(timeInMillis);
      }

      @Override public void onProgressChanged(int progress) {

        float percent = (float) progress / (float) 100;
        int timeInMillis = (int) (percent * audioView.getHVPlayableDuration());
        audioView.seekToHVPlayable(timeInMillis);
      }
    });

    prepareAsync();
  }

  /**
   * 创建视频播放器
   *
   * @param videoUrl 媒体的地址
   */
  public void buildVideoPlayer(String videoUrl) {

    removeAllViews();

    HVVideoPlayerFactory videoPlayerFactory = new HVVideoPlayerFactory(getContext());

    final HVVideoView videoView = (HVVideoView) videoPlayerFactory.createPlayable();
    videoView.setVideoURI(Uri.parse(videoUrl));
    addPlayable(videoView);

    final HVVideoController videoController =
        (HVVideoController) videoPlayerFactory.createController();
    addController(videoController);

    videoView.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          hideOrShowMediaController(videoController);
        }
        return false;
      }
    });

    videoView.setCallback(new HVPlayable.Callback() {

      @Override public void onPrepared() {
        mHVMediaPlayerContext.onPrepared();
      }

      @Override public void onProgressChanged(int percent, int secondaryProgress) {

        if (!videoController.isDraggingSeekBar()) {
          videoController.setSeekBarProgress(percent);
          videoController.setSeekBarSecondaryProgress(secondaryProgress);
          videoController.setCurrentTime(videoView.getHVPlayableCurrentPosition());
        }
      }

      @Override public void onCompletion(MediaPlayer mediaPlayer) {
        videoController.setCurrentTime(0);
        videoController.setSeekBarProgress(0);

        videoController.hidePauseButton();
        videoController.showPlayButton();

        videoView.seekToHVPlayable(0);
        videoView.stopPlayableTimer();
      }

      @Override public void onError(MediaPlayer mediaPlayer) {

      }
    });

    videoController.setCallback(new HVVideoController.Callback() {

      @Override public void start() {
        if (!videoView.isHVPlayablePlaying()) {
          mHVMediaPlayerContext.start();
        }
      }

      @Override public void pause() {
        if (videoView.isHVPlayablePlaying()) {
          mHVMediaPlayerContext.pause();
        }
      }

      @Override public void shrink() {
        if (mCallback != null && videoController.isEnterFullScreen()) {
          mCallback.onExitScreen();
          videoController.setIsEnterFullScreen(false);
        }
      }

      @Override public void expand() {
        if (mCallback != null && !videoController.isEnterFullScreen()) {
          mCallback.onEnterFullScreen();
          videoController.setIsEnterFullScreen(true);
        }
      }

      @Override public void updateCurrentTimeWhenDragging(int progress) {
        float percent = (float) progress / (float) 100;
        int timeInMillis = (int) (percent * videoView.getHVPlayableDuration());
        videoController.setCurrentTime(timeInMillis);
      }

      @Override public void onProgressChanged(int progress) {
        float percent = (float) progress / (float) 100;
        int timeInMillis = (int) (percent * videoView.getHVPlayableDuration());
        videoView.seekToHVPlayable(timeInMillis);
      }
    });

    prepareAsync();
  }

  private void init() {
    setBackgroundColor(Color.BLACK); // 设置播放器背景为“纯黑”
  }

  private void prepareAsync() {
    mHVMediaPlayerContext = new HVMediaPlayerContext(mHVPlayable, mHVController);
    mHVMediaPlayerContext.setPlayerState(mHVMediaPlayerContext.getPreparingState());
    mHVMediaPlayerContext.prepareAsync();
  }

  private void addPlayable(View playable) {

    LayoutParams videoViewLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    videoViewLayoutParams.addRule(CENTER_IN_PARENT);
    videoViewLayoutParams.leftMargin = DensityUtil.dip2px(getContext(), 8);
    videoViewLayoutParams.rightMargin = DensityUtil.dip2px(getContext(), 8);
    playable.setLayoutParams(videoViewLayoutParams);
    addView(playable);

    mHVPlayable = (HVPlayable) playable;
  }

  private void addController(View controller) {

    LayoutParams controllerLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 40));
    controllerLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
    controller.setLayoutParams(controllerLayoutParams);
    addView(controller);

    mHVController = (HVController) controller;
  }

  /**
   * 控制“操作栏”的隐藏和显示
   */
  private void hideOrShowMediaController(View controller) {

    if (mIsMediaControllerHidden) {
      controller.animate()
          .translationY(0L)
          .setInterpolator(new DecelerateInterpolator(2.0F))
          .setDuration(500L)
          .start();
    } else {
      controller.animate()
          .translationY(controller.getHeight())
          .setInterpolator(new AccelerateInterpolator(2.0F))
          .setDuration(500L)
          .start();
    }

    mIsMediaControllerHidden = !mIsMediaControllerHidden;
  }

  /**
   * HVMediaPlayer 的回调接口
   */
  public interface Callback {

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
