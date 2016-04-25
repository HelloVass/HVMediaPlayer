package org.geeklub.hvmediaplayer.widgets.audio;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import org.geeklub.hvmediaplayer.R;
import org.geeklub.hvmediaplayer.utils.TimeUtil;
import org.geeklub.hvmediaplayer.widgets.audio.support_commands.Command;

/**
 * Created by HelloVass on 16/4/6.
 */
public class HVAudioController extends FrameLayout {

  private Command mStartCommand;

  private Command mPauseCommand;

  private ImageView mPlayPauseButton;

  private TextView mCurrentTime;

  private TextView mEndTime;

  private SeekBar mSeekBar;

  private Mediator mHVAudioPlayer;

  private boolean mIsDraggingSeekBar = false;

  public HVAudioController(Context context) {
    super(context);
    init();
  }

  public void setHVAudioPlayer(Mediator HVAudioPlayer) {
    mHVAudioPlayer = HVAudioPlayer;
  }

  /**
   * 重置操作栏
   */
  public void reset() {
    displayPlayImg();
    setCurrentTime(0);
    setSeekBarProgress(0);
    setSeekBarSecondaryProgress(0);
    setEndTime(0);
    mIsDraggingSeekBar = false;
    setTranslationY(0.0F);
  }

  /**
   * SeekBar 是否正在被用户拖动ing
   *
   * @return 如果正在被拖动，返回true
   */
  public boolean isDraggingSeekBar() {
    return mIsDraggingSeekBar;
  }

  public void play() {
    if (mStartCommand != null) {
      mStartCommand.execute();
    }
  }

  public void pause() {
    if (mPauseCommand != null) {
      mPauseCommand.execute();
    }
  }

  public void show() {
    setVisibility(VISIBLE);
  }

  public void hide() {
    setVisibility(GONE);
  }

  public void setStartCommand(Command startCommand) {
    mStartCommand = startCommand;
  }

  public void setPauseCommand(Command pauseCommand) {
    mPauseCommand = pauseCommand;
  }

  public void displayPlayImg() {
    mPlayPauseButton.setImageResource(R.mipmap.ic_play_arrow_white_24dp);
  }

  public void displayPauseImg() {
    mPlayPauseButton.setImageResource(R.mipmap.ic_pause_white_24dp);
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

    View.inflate(getContext(), R.layout.layout_hv_audio_controller, this);

    mPlayPauseButton = (ImageView) findViewById(R.id.iv_play);
    mCurrentTime = (TextView) findViewById(R.id.tv_current);
    mSeekBar = (SeekBar) findViewById(R.id.sb_progress);
    mEndTime = (TextView) findViewById(R.id.tv_end);

    setUpCallbacks();
  }

  private void setUpCallbacks() {

    mPlayPauseButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mHVAudioPlayer != null) {
          mHVAudioPlayer.doPlayPause();
        }
      }
    });

    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) {
          return;
        }

        if (mHVAudioPlayer != null) {
          mHVAudioPlayer.updateCurrentTimeWhenDragging(progress);
        }
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDraggingSeekBar = true;
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

        mIsDraggingSeekBar = false;

        if (mHVAudioPlayer != null) {
          mHVAudioPlayer.seekToStopTrackingTouchPosition(seekBar.getProgress());
        }
      }
    });
  }
}
