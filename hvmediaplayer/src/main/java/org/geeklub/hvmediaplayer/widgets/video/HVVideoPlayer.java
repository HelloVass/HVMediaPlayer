package org.geeklub.hvmediaplayer.widgets.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.Command;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.ExpandCommand;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.PauseCommand;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.ShrinkCommand;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.StartCommand;

/**
 * Created by HelloVass on 16/4/13.
 */
public class HVVideoPlayer extends RelativeLayout implements Mediator {

  private static final String TAG = HVVideoPlayer.class.getSimpleName();

  private Context mContext;

  private String mVideoUrl;

  private ViewGroup.LayoutParams mLayoutParams;

  private HVVideoView mHVVideoView;

  private HVVideoController mHVVideoController;

  private boolean mIsEnterFullScreen = false;

  private boolean mIsControllerHidden = false;

  private HVVideoPlayer(Builder builder) {
    super(builder.mContext);

    mContext = builder.mContext;
    mLayoutParams = builder.mLayoutParams;
    mVideoUrl = builder.mVideoUrl;

    init();
  }

  /**
   * 是否被添加到“Content”中
   *
   * @return 如果被添加到“Content”中，则返回true
   */
  public boolean isAddedToContent() {
    return getParent() != null;
  }

  @Override public void doPlayPause() {

    if (mHVVideoView.isPlaying()) { // 如果正在播放中
      mHVVideoController.pause();
    } else { // 如果停止播放了
      mHVVideoController.play();
    }

    updatePausePlay();
  }

  @Override public void doExpandShrink() {

    if (mIsEnterFullScreen) { // 如果当前是全屏
      mHVVideoController.exitFullScreen();
    } else { // 如果已经退出全屏了
      mHVVideoController.enterFullScreen();
    }

    mIsEnterFullScreen = !mIsEnterFullScreen;
    updateShrinkExpand();
  }

  @Override public void updateCurrentTimeWhenDragging(int progress) {

    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVVideoView.getDuration());
    mHVVideoController.setCurrentTime(timeInMillis);
  }

  @Override public void seekToStopTrackingTouchPosition(int progress) {

    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVVideoView.getDuration());
    mHVVideoView.seekTo(timeInMillis);
  }

  @Override public void updateCurrentTimeWhenPlaying(int progress, int bufferPercentage) {

    if (!mHVVideoController.isDraggingSeekBar()) {
      mHVVideoController.setSeekBarProgress(progress);
      mHVVideoController.setSeekBarSecondaryProgress(bufferPercentage);
      mHVVideoController.setCurrentTime(mHVVideoView.getCurrentPosition());
    }
  }

  @Override public void onPrepared(MediaPlayer mp) {

    mHVVideoController.setCurrentTime(0);
    mHVVideoController.setEndTime(mHVVideoView.getDuration());

    mHVVideoController.setSeekBarProgress(0);
    mHVVideoController.setSeekBarSecondaryProgress(0);
  }

  @Override public void onCompletion(MediaPlayer mp) {

    mHVVideoController.setCurrentTime(0);
    mHVVideoController.setSeekBarProgress(0);
    mHVVideoController.setSeekBarSecondaryProgress(0);
    mHVVideoController.displayPlayImg();

    mHVVideoView.stopTimer();
    mHVVideoView.seekTo(0);
  }

  @Override public void onError(MediaPlayer mp, int what, int extra) {

  }

  private void updatePausePlay() {

    if (mHVVideoView.isPlaying()) {
      mHVVideoController.displayPauseImg();
    } else {
      mHVVideoController.displayPlayImg();
    }
  }

  private void updateShrinkExpand() {

    if (mIsEnterFullScreen) {
      mHVVideoController.displayShrinkImg();
    } else {
      mHVVideoController.displayExpandImg();
    }
  }

  private void hideOrShowMediaController() {

    if (mHVVideoController == null) {
      return;
    }

    if (mIsControllerHidden) {
      mHVVideoController.animate()
          .translationY(0L)
          .setInterpolator(new DecelerateInterpolator(2.0F))
          .setDuration(500L)
          .start();
    } else {
      mHVVideoController.animate()
          .translationY(mHVVideoController.getHeight())
          .setInterpolator(new AccelerateInterpolator(2.0F))
          .setDuration(500L)
          .start();
    }

    mIsControllerHidden = !mIsControllerHidden;
  }

  private void init() {

    setLayoutParams(mLayoutParams);
    setBackgroundColor(Color.BLACK);

    // 创建接受者
    mHVVideoView = new HVVideoView(mContext);
    mHVVideoView.setVideoPath(mVideoUrl);
    mHVVideoView.setHVVideoPlayer(this);

    mHVVideoView.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          hideOrShowMediaController();
        }
        return false;
      }
    });

    // 创建请求者
    mHVVideoController = new HVVideoController(mContext);
    mHVVideoController.setHVVideoPlayer(this);

    // 构造命令
    Command startCommand = new StartCommand(mHVVideoView);
    Command pauseCommand = new PauseCommand(mHVVideoView);
    Command expandCommand = new ExpandCommand((Activity) mContext, getLayoutParams());
    Command shrinkCommand = new ShrinkCommand((Activity) mContext, getLayoutParams());

    // 设置命令
    mHVVideoController.setStartCommand(startCommand);
    mHVVideoController.setPauseCommand(pauseCommand);
    mHVVideoController.setExpandCommand(expandCommand);
    mHVVideoController.setShrinkCommand(shrinkCommand);

    addPlayable();
    addController();
  }

  private void addPlayable() {
    LayoutParams videoViewLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    videoViewLayoutParams.addRule(CENTER_IN_PARENT);
    videoViewLayoutParams.leftMargin = DensityUtil.dip2px(getContext(), 8);
    videoViewLayoutParams.rightMargin = DensityUtil.dip2px(getContext(), 8);
    mHVVideoView.setLayoutParams(videoViewLayoutParams);
    addView(mHVVideoView);
  }

  private void addController() {
    LayoutParams controllerLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 40));
    controllerLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
    mHVVideoController.setLayoutParams(controllerLayoutParams);
    addView(mHVVideoController);
  }

  public static class Builder {

    private Context mContext;

    private String mVideoUrl;

    private ViewGroup.LayoutParams mLayoutParams;

    public Builder(Context context) {
      mContext = context;
    }

    public Builder setVideoUrl(String videoUrl) {
      mVideoUrl = videoUrl;
      return this;
    }

    public Builder setLayoutParams(ViewGroup.LayoutParams layoutParams) {
      mLayoutParams = layoutParams;
      return this;
    }

    public HVVideoPlayer build() {
      return new HVVideoPlayer(this);
    }
  }
}
