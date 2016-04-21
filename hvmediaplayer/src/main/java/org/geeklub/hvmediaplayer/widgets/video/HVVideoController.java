package org.geeklub.hvmediaplayer.widgets.video;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import org.geeklub.hvmediaplayer.widgets.video.support_commands.Command;
import org.geeklub.hvmediaplayer.R;
import org.geeklub.hvmediaplayer.utils.TimeUtil;

/**
 * Created by HelloVass on 16/4/6.
 */
public class HVVideoController extends FrameLayout {

  private Command mStartCommand;

  private Command mPauseCommand;

  private Command mExpandCommand;

  private Command mShrinkCommand;

  private ImageView mPlayPauseButton;

  private TextView mCurrentTime;

  private TextView mEndTime;

  private SeekBar mSeekBar;

  private ImageView mExpandShrinkButton;

  private Mediator mHVVideoPlayer;

  private boolean mIsDraggingSeekBar = false;

  public HVVideoController(Context context) {
    super(context);
    init();
  }

  public void setHVVideoPlayer(Mediator HVVideoPlayer) {
    mHVVideoPlayer = HVVideoPlayer;
  }

  public void show() {
    setVisibility(VISIBLE);
  }

  public void hide() {
    setVisibility(GONE);
  }

  /**
   * SeekBar 是否正在被用户拖动ing
   *
   * @return 如果正在被拖动，返回true
   */
  public boolean isDraggingSeekBar() {
    return mIsDraggingSeekBar;
  }

  /**
   * 执行播放命令
   */
  public void play() {
    if (mStartCommand != null) {
      mStartCommand.execute();
    }
  }

  /**
   * 执行暂停命令
   */
  public void pause() {
    if (mPauseCommand != null) {
      mPauseCommand.execute();
    }
  }

  /**
   * 执行全屏命令
   */
  public void enterFullScreen() {
    if (mExpandCommand != null) {
      mExpandCommand.execute();
    }
  }

  /**
   * 执行退出全屏命令
   */
  public void exitFullScreen() {
    if (mShrinkCommand != null) {
      mShrinkCommand.execute();
    }
  }

  public void setStartCommand(Command startCommand) {
    mStartCommand = startCommand;
  }

  public void setPauseCommand(Command pauseCommand) {
    mPauseCommand = pauseCommand;
  }

  public void setExpandCommand(Command expandCommand) {
    mExpandCommand = expandCommand;
  }

  public void setShrinkCommand(Command shrinkCommand) {
    mShrinkCommand = shrinkCommand;
  }

  public void displayPlayImg() {
    mPlayPauseButton.setImageResource(R.mipmap.ic_play_circle_filled_white_24dp);
  }

  public void displayPauseImg() {
    mPlayPauseButton.setImageResource(R.mipmap.ic_pause_circle_filled_white_24dp);
  }

  public void displayExpandImg() {
    mExpandShrinkButton.setImageResource(R.mipmap.ic_fullscreen_white_24dp);
  }

  public void displayShrinkImg() {
    mExpandShrinkButton.setImageResource(R.mipmap.ic_fullscreen_exit_white_24dp);
  }

  /**
   * 更新当前的时间
   *
   * @param timeInMillis 当前时间
   */
  public void setCurrentTime(long timeInMillis) {
    mCurrentTime.setText(TimeUtil.getTime(timeInMillis));
  }

  /**
   * 视频总长度
   *
   * @param timeInMillis 总时长
   */
  public void setEndTime(long timeInMillis) {
    mEndTime.setText(TimeUtil.getTime(timeInMillis));
  }

  /**
   * 设置当前播放进度
   *
   * @param progress 播放进度
   */
  public void setSeekBarProgress(int progress) {
    mSeekBar.setProgress(progress);
  }

  /**
   * 设置缓冲进度
   *
   * @param secondaryProgress 缓冲进度
   */
  public void setSeekBarSecondaryProgress(int secondaryProgress) {
    mSeekBar.setSecondaryProgress(secondaryProgress);
  }

  private void init() {

    View.inflate(getContext(), R.layout.layout_hv_video_controller, this);

    mPlayPauseButton = (ImageView) findViewById(R.id.iv_play);
    mCurrentTime = (TextView) findViewById(R.id.tv_current);
    mSeekBar = (SeekBar) findViewById(R.id.sb_progress);
    mEndTime = (TextView) findViewById(R.id.tv_end);
    mExpandShrinkButton = (ImageView) findViewById(R.id.iv_expand);

    setUpCallbacks();
  }

  private void setUpCallbacks() {

    mPlayPauseButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mHVVideoPlayer != null) {
          mHVVideoPlayer.doPlayPause();
        }
      }
    });

    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) {
          return;
        }

        if (mHVVideoPlayer != null) {
          mHVVideoPlayer.updateCurrentTimeWhenDragging(progress);
        }
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDraggingSeekBar = true;
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

        mIsDraggingSeekBar = false;

        if (mHVVideoPlayer != null) {
          mHVVideoPlayer.seekToStopTrackingTouchPosition(seekBar.getProgress());
        }
      }
    });

    mExpandShrinkButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mHVVideoPlayer != null) {
          mHVVideoPlayer.doExpandShrink();
        }
      }
    });
  }
}
