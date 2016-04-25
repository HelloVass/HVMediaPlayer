package org.geeklub.hvmediaplayer.widgets.audio;

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
import org.geeklub.hvmediaplayer.imageloader.ImageLoader;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.audio.support_commands.Command;
import org.geeklub.hvmediaplayer.widgets.audio.support_commands.PauseCommand;
import org.geeklub.hvmediaplayer.widgets.audio.support_commands.StartCommand;

/**
 * Created by HelloVass on 16/4/13.
 */
public class HVAudioPlayer extends RelativeLayout implements Mediator, HVAudioPlayerInterface {

  private Context mContext;

  private String mAudioUrl;

  private String mCoverImageUrl;

  private ImageLoader mImageLoader;

  private ViewGroup.LayoutParams mLayoutParams;

  private HVAudioView mHVAudioView;

  private HVAudioController mHVAudioController;

  private ImageView mCloseButton;

  private boolean mIsControllerHidden = false;

  private HVAudioPlayerInterface.OnDismissListener mOnDismissListener;

  private HVAudioPlayer(Builder builder) {
    super(builder.mContext);

    mContext = builder.mContext;
    mAudioUrl = builder.mAudioUrl;
    mCoverImageUrl = builder.mCoverImageUrl;
    mImageLoader = builder.mImageLoader;
    mLayoutParams = builder.mLayoutParams;
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
   * 重新加载
   */
  public void reload() {
    setVisibility(VISIBLE);
    mHVAudioView.resume();
  }

  /**
   * 关闭播放器
   */
  @Override public void close() {

    mHVAudioView.stopTimer();
    mHVAudioView.stopPlayback();

    mHVAudioController.hide();
    mHVAudioController.reset();

    mIsControllerHidden = false;
    setVisibility(GONE);
  }

  /**
   * 销毁播放器的时候调用
   */
  public void onDestroy() {
    mHVAudioView.stopTimer();
    mHVAudioView.stopPlayback();
  }

  @Override public void doPlayPause() {

    if (mHVAudioView.isPlaying()) { // 如果正在播放中
      mHVAudioController.pause();
      mHVAudioController.displayPlayImg();
    } else {
      mHVAudioController.play();
      mHVAudioController.displayPauseImg();
    }
  }

  /**
   * 用户拖动SeekBar的时候更新当前时间
   *
   * @param progress 当前的进度
   */
  @Override public void updateCurrentTimeWhenDragging(int progress) {

    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVAudioView.getDuration());
    mHVAudioController.setCurrentTime(timeInMillis);
  }

  /**
   * 用户停止拖动后，将播放位置移动到指定的位置
   *
   * @param progress 当前的进度
   */
  @Override public void seekToStopTrackingTouchPosition(int progress) {

    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVAudioView.getDuration());
    mHVAudioView.seekTo(timeInMillis);
  }

  /**
   * 在播放的时候，更新当前时间
   *
   * @param progress 当前的进度
   * @param bufferPercentage 缓冲的进度
   */
  @Override public void updateCurrentTimeWhenPlaying(int progress, int bufferPercentage) {

    if (!mHVAudioController.isDraggingSeekBar()) {
      mHVAudioController.setSeekBarProgress(progress);
      mHVAudioController.setSeekBarSecondaryProgress(bufferPercentage);
      mHVAudioController.setCurrentTime(mHVAudioView.getCurrentPosition());
    }
  }

  /**
   * 播放器准备完毕
   *
   * @param mp MediaPlayer
   */
  @Override public void onPrepared(MediaPlayer mp) {

    mHVAudioController.show();

    mHVAudioController.displayPlayImg();
    mHVAudioController.setCurrentTime(0);
    mHVAudioController.setSeekBarProgress(0);
    mHVAudioController.setSeekBarSecondaryProgress(0);
    mHVAudioController.setEndTime(mHVAudioView.getDuration());
  }

  /**
   * 播放结束
   *
   * @param mp MediaPlayer
   */
  @Override public void onCompletion(MediaPlayer mp) {

    mHVAudioController.displayPlayImg();
    mHVAudioController.setCurrentTime(0);
    mHVAudioController.setSeekBarProgress(0);
    mHVAudioController.setSeekBarSecondaryProgress(0);

    mHVAudioView.stopTimer();
    mHVAudioView.seekTo(0);
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

    // setup AudioView
    mHVAudioView = new HVAudioView(mContext);
    mHVAudioView.setAudioPath(mAudioUrl);
    mHVAudioView.setImageLoader(mImageLoader);
    mHVAudioView.setCoverImagePath(mCoverImageUrl);
    mHVAudioView.setHVAudioPlayer(this);
    mHVAudioView.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          hideOrShowMediaController();
        }
        return false;
      }
    });

    // setup controller
    mHVAudioController = new HVAudioController(mContext);
    mHVAudioController.setHVAudioPlayer(this);
    mHVAudioController.hide();
    setControllerSupportCommands();

    // setup close button
    mCloseButton = new ImageView(mContext);
    mCloseButton.setImageResource(R.mipmap.ic_close_white_24dp);
    mCloseButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        close();
        if (mOnDismissListener != null) {
          mOnDismissListener.dismiss(HVAudioPlayer.this);
        }
      }
    });

    addPlayable();
    addController();
    addCloseButton();
  }

  private void setControllerSupportCommands() {
    Command startCommand = new StartCommand(mHVAudioView);
    Command pauseCommand = new PauseCommand(mHVAudioView);
    mHVAudioController.setStartCommand(startCommand);
    mHVAudioController.setPauseCommand(pauseCommand);
  }

  private void addPlayable() {
    LayoutParams videoViewLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    videoViewLayoutParams.addRule(CENTER_IN_PARENT);
    videoViewLayoutParams.leftMargin = DensityUtil.dip2px(getContext(), 8);
    videoViewLayoutParams.rightMargin = DensityUtil.dip2px(getContext(), 8);
    mHVAudioView.setLayoutParams(videoViewLayoutParams);
    addView(mHVAudioView);
  }

  private void addController() {
    LayoutParams controllerLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 40));
    controllerLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
    mHVAudioController.setLayoutParams(controllerLayoutParams);
    addView(mHVAudioController);
  }

  private void addCloseButton() {
    LayoutParams closeButtonLayoutParams =
        new LayoutParams(DensityUtil.dip2px(mContext, 24), DensityUtil.dip2px(mContext, 24));
    closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    closeButtonLayoutParams.setMargins(0, DensityUtil.dip2px(mContext, 16),
        DensityUtil.dip2px(mContext, 16), 0);
    mCloseButton.setLayoutParams(closeButtonLayoutParams);
    mCloseButton.setClickable(true);
    addView(mCloseButton);
  }

  private void hideOrShowMediaController() {

    if (mHVAudioController == null) {
      return;
    }

    if (!mHVAudioController.isShown()) {
      return;
    }

    if (mIsControllerHidden) {
      mHVAudioController.animate()
          .translationY(0L)
          .setInterpolator(new DecelerateInterpolator(2.0F))
          .setDuration(500L)
          .start();
    } else {
      mHVAudioController.animate()
          .translationY(mHVAudioController.getHeight())
          .setInterpolator(new AccelerateInterpolator(2.0F))
          .setDuration(500L)
          .start();
    }

    mIsControllerHidden = !mIsControllerHidden;
  }

  public static class Builder {

    private Context mContext;

    private String mAudioUrl;

    private String mCoverImageUrl;

    private ImageLoader mImageLoader;

    private ViewGroup.LayoutParams mLayoutParams;

    private HVAudioPlayerInterface.OnDismissListener mOnDismissListener;

    public Builder(Context context) {
      mContext = context;
    }

    public Builder setAudioUrl(String audioUrl) {
      mAudioUrl = audioUrl;
      return this;
    }

    public Builder setCoverImageUrl(String coverImageUrl) {
      mCoverImageUrl = coverImageUrl;
      return this;
    }

    public Builder setImageLoader(ImageLoader imageLoader) {
      mImageLoader = imageLoader;
      return this;
    }

    public Builder setLayoutParams(ViewGroup.LayoutParams layoutParams) {
      mLayoutParams = layoutParams;
      return this;
    }

    public Builder setOnDismissListener(
        HVAudioPlayerInterface.OnDismissListener onDismissListener) {
      mOnDismissListener = onDismissListener;
      return this;
    }

    public HVAudioPlayer build() {
      return new HVAudioPlayer(this);
    }
  }
}
