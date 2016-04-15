package org.geeklub.hvmediaplayer.widgets.audio;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import org.geeklub.hvmediaplayer.imageloader.ImageLoader;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.audio.support_commands.Command;
import org.geeklub.hvmediaplayer.widgets.audio.support_commands.PauseCommand;
import org.geeklub.hvmediaplayer.widgets.audio.support_commands.StartCommand;

/**
 * Created by HelloVass on 16/4/13.
 */
public class HVAudioPlayer extends RelativeLayout implements Mediator {

  private Context mContext;

  private String mAudioUrl;

  private String mCoverImageUrl;

  private ImageLoader mImageLoader;

  private HVAudioView mHVAudioView;

  private HVAudioController mHVAudioController;

  private boolean mIsControllerHidden = false;

  private HVAudioPlayer(Builder builder) {
    super(builder.mContext);

    mContext = builder.mContext;
    mAudioUrl = builder.mAudioUrl;
    mCoverImageUrl = builder.mCoverImageUrl;
    mImageLoader = builder.mImageLoader;

    init();
  }

  public void init() {

    setBackgroundColor(Color.BLACK);

    // 创建接受者
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

    // 创建请求者
    mHVAudioController = new HVAudioController(mContext);

    mHVAudioController.setHVAudioPlayer(this);
    // 构造命令
    Command startCommand = new StartCommand(mHVAudioView);
    Command pauseCommand = new PauseCommand(mHVAudioView);

    // 设置命令
    mHVAudioController.setStartCommand(startCommand);
    mHVAudioController.setPauseCommand(pauseCommand);

    addPlayable();
    addController();
  }

  @Override public void doPlayPause() {

    if (mHVAudioView.isPlaying()) {
      mHVAudioController.pause();
    } else {
      mHVAudioController.play();
    }

    updatePausePlay();
  }

  @Override public void updateCurrentTimeWhenDragging(int progress) {

    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVAudioView.getDuration());
    mHVAudioController.setCurrentTime(timeInMillis);
  }

  @Override public void seekToStopTrackingTouchPosition(int progress) {

    float percent = (float) progress / (float) 100;
    int timeInMillis = (int) (percent * mHVAudioView.getDuration());
    mHVAudioView.seekTo(timeInMillis);
  }

  @Override public void updateCurrentTimeWhenPlaying(int progress, int bufferPercentage) {

    if (!mHVAudioController.isDraggingSeekBar()) {
      mHVAudioController.setSeekBarProgress(progress);
      mHVAudioController.setSeekBarSecondaryProgress(bufferPercentage);
      mHVAudioController.setCurrentTime(mHVAudioView.getCurrentPosition());
    }
  }

  @Override public void onPrepared(MediaPlayer mp) {

    mHVAudioController.setCurrentTime(0);
    mHVAudioController.setEndTime(mHVAudioView.getDuration());

    mHVAudioController.setSeekBarProgress(0);
    mHVAudioController.setSeekBarSecondaryProgress(0);
  }

  @Override public void onCompletion(MediaPlayer mp) {

    mHVAudioController.setCurrentTime(0);
    mHVAudioController.setSeekBarProgress(0);
    mHVAudioController.setSeekBarSecondaryProgress(0);
    mHVAudioController.displayPlayImg();

    mHVAudioView.stopTimer();
    mHVAudioView.seekTo(0);
  }

  @Override public void onError(MediaPlayer mp, int what, int extra) {

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

  private void updatePausePlay() {

    if (mHVAudioView.isPlaying()) {
      mHVAudioController.displayPauseImg();
    } else {
      mHVAudioController.displayPlayImg();
    }
  }

  private void hideOrShowMediaController() {

    if (mHVAudioController == null) {
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

    public HVAudioPlayer build() {
      return new HVAudioPlayer(this);
    }
  }
}
