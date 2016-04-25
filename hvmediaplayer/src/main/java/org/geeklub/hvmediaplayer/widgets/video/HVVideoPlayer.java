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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import org.geeklub.hvmediaplayer.R;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.Command;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.ExpandCommand;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.PauseCommand;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.ShrinkCommand;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.StartCommand;

/**
 * Created by HelloVass on 16/4/13.
 */
public class HVVideoPlayer extends RelativeLayout implements Mediator, HVVideoPlayerInterface {

  private static final String TAG = HVVideoPlayer.class.getSimpleName();

  private Context mContext;

  private String mVideoUrl;

  private ViewGroup.LayoutParams mLayoutParams;

  private HVVideoView mHVVideoView;

  private HVVideoController mHVVideoController;

  private ImageView mCloseButton;

  private boolean mIsControllerHidden = false;

  private int mCurrentPosition = 0;

  private HVVideoPlayerInterface.OnDismissListener mOnDismissListener;

  private HVVideoPlayer(Builder builder) {
    super(builder.mContext);

    mContext = builder.mContext;
    mLayoutParams = builder.mLayoutParams;
    mVideoUrl = builder.mVideoUrl;
    mOnDismissListener = builder.mOnDismissListener;
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

  /**
   * 当前的播放进度是否被保存了
   *
   * @return 如果已经保存，则返回true
   */
  public boolean isCurrentPositionSaved() {
    return mCurrentPosition > 0;
  }

  /**
   * 重新加载
   */
  public void reload() {
    setVisibility(VISIBLE);
    mHVVideoView.resume();
  }

  /**
   * 保存当前的播放进度
   */
  public void save() {
    mCurrentPosition = mHVVideoView.getCurrentPosition();
    mHVVideoController.pause();
    updatePausePlay();
  }

  /**
   * 恢复之前的播放进度
   */
  public void restore() {
    if (isCurrentPositionSaved()) {
      mHVVideoView.seekTo(mCurrentPosition);
    }
  }

  /**
   * 关闭播放器
   */
  @Override public void close() {
    changeScreenOrientationToPortrait(); // 将屏幕的方向恢复为竖直

    mHVVideoView.stopTimer();
    mHVVideoView.stopPlayback();

    mHVVideoController.hide();
    mHVVideoController.reset();

    mIsControllerHidden = false;
    mCurrentPosition = 0;
    setVisibility(GONE);
  }

  /**
   * 销毁播放器的时候调用
   */
  public void onDestroy() {
    mHVVideoView.stopTimer();
  }

  @Override public void doPlayPause() {

    if (mHVVideoView.isPlaying()) { // 如果正在播放中
      mHVVideoController.pause();
      mHVVideoController.displayPlayImg();
    } else {
      mHVVideoController.play();
      mHVVideoController.displayPauseImg();
    }
  }

  @Override public void doExpandShrink() {

    if (mHVVideoController.isEnterFullScreen()) { // 如果当前是全屏
      mHVVideoController.exitFullScreen();
    } else { // 如果已经退出全屏
      mHVVideoController.enterFullScreen();
    }

    updateShrinkExpand();
  }

  /**
   * 用户拖动SeekBar的时候更新当前时间
   *
   * @param progress 当前的进度
   */
  @Override public void updateCurrentTimeWhenDragging(int progress) {
    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVVideoView.getDuration());
    mHVVideoController.setCurrentTime(timeInMillis);
  }

  /**
   * 用户停止拖动后，将播放位置移动到指定的位置
   *
   * @param progress 当前的进度
   */
  @Override public void seekToStopTrackingTouchPosition(int progress) {

    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVVideoView.getDuration());
    mHVVideoView.seekTo(timeInMillis);
  }

  /**
   * 在播放的时候，更新当前时间
   *
   * @param progress 当前的进度
   * @param bufferPercentage 缓冲的进度
   */
  @Override public void updateCurrentTimeWhenPlaying(int progress, int bufferPercentage) {

    if (!mHVVideoController.isDraggingSeekBar()) {
      mHVVideoController.setSeekBarProgress(progress);
      mHVVideoController.setSeekBarSecondaryProgress(bufferPercentage);
      mHVVideoController.setCurrentTime(mHVVideoView.getCurrentPosition());
    }
  }

  /**
   * 播放器准备完毕
   *
   * @param mp MediaPlayer
   */
  @Override public void onPrepared(MediaPlayer mp) {

    mHVVideoController.show();

    if (isCurrentPositionSaved()) { // 如果之前的播放进度被保存了，则不更新Controller的UI
      return;
    }

    mHVVideoController.displayPlayImg();
    mHVVideoController.setCurrentTime(0);
    mHVVideoController.setSeekBarProgress(0);
    mHVVideoController.setSeekBarSecondaryProgress(0);
    mHVVideoController.setEndTime(mHVVideoView.getDuration());
    mHVVideoController.displayExpandImg();
  }

  /**
   * 播放结束
   *
   * @param mp MediaPlayer
   */
  @Override public void onCompletion(MediaPlayer mp) {

    mHVVideoController.displayPlayImg();
    mHVVideoController.setCurrentTime(0);
    mHVVideoController.setSeekBarProgress(0);
    mHVVideoController.setSeekBarSecondaryProgress(0);

    mHVVideoView.stopTimer();
    mHVVideoView.seekTo(0);
  }

  /**
   * 播放出错
   *
   * @param mp MediaPlayer
   */
  @Override public void onError(MediaPlayer mp, int what, int extra) {

  }

  private void init() {

    setLayoutParams(mLayoutParams);
    setBackgroundColor(Color.BLACK);

    // setup VideoView
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

    // setup controller
    mHVVideoController = new HVVideoController(mContext);
    mHVVideoController.setHVVideoPlayer(this);
    mHVVideoController.hide();
    setControllerSupportCommands();

    // setup close button
    mCloseButton = new ImageView(mContext);
    mCloseButton.setImageResource(R.mipmap.ic_close_white_24dp);
    mCloseButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        close();
        if (mOnDismissListener != null) {
          mOnDismissListener.dismiss(HVVideoPlayer.this);
        }
      }
    });

    addPlayable();
    addController();
    addCloseButton();
  }

  private void setControllerSupportCommands() {

    Command startCommand = new StartCommand(mHVVideoView);
    Command pauseCommand = new PauseCommand(mHVVideoView);
    Command expandCommand = new ExpandCommand((Activity) mContext, getLayoutParams());
    Command shrinkCommand = new ShrinkCommand((Activity) mContext, getLayoutParams());

    mHVVideoController.setStartCommand(startCommand);
    mHVVideoController.setPauseCommand(pauseCommand);
    mHVVideoController.setExpandCommand(expandCommand);
    mHVVideoController.setShrinkCommand(shrinkCommand);
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

  private void addCloseButton() {
    LayoutParams closeButtonLayoutParams =
        new LayoutParams(DensityUtil.dip2px(mContext, 24), DensityUtil.dip2px(mContext, 24));
    closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    closeButtonLayoutParams.setMargins(0, DensityUtil.dip2px(mContext, 16),
        DensityUtil.dip2px(mContext, 16), 0);
    mCloseButton.setLayoutParams(closeButtonLayoutParams);
    addView(mCloseButton);
  }

  /**
   * 更新播放、暂停按钮
   */
  private void updatePausePlay() {

    if (mHVVideoView.isPlaying()) {
      mHVVideoController.displayPauseImg();
    } else {
      mHVVideoController.displayPlayImg();
    }
  }

  /**
   * 更细全屏、退出全屏按钮
   */
  private void updateShrinkExpand() {

    if (mHVVideoController.isEnterFullScreen()) {
      mHVVideoController.displayShrinkImg();
    } else {
      mHVVideoController.displayExpandImg();
    }
  }

  private void changeScreenOrientationToPortrait() {

    if (!isAddedToContent()) {
      return;
    }

    if (!mHVVideoController.isEnterFullScreen()) {
      return;
    }

    mHVVideoController.exitFullScreen();
  }

  private void hideOrShowMediaController() {

    if (mHVVideoController == null) {
      return;
    }

    if (!mHVVideoController.isShown()) {
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

  public static class Builder {

    private Context mContext;

    private String mVideoUrl;

    private ViewGroup.LayoutParams mLayoutParams;

    private HVVideoPlayerInterface.OnDismissListener mOnDismissListener;

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

    public Builder setOnDismissListener(
        HVVideoPlayerInterface.OnDismissListener onDismissListener) {
      mOnDismissListener = onDismissListener;
      return this;
    }

    public HVVideoPlayer build() {
      return new HVVideoPlayer(this);
    }
  }
}
