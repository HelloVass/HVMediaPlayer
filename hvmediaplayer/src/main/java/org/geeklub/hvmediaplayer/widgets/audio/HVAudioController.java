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

  public boolean isDraggingSeekBar() {
    return mIsDraggingSeekBar;
  }

  public void play() {
    mStartCommand.execute();
  }

  public void pause() {
    mPauseCommand.execute();
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

  public void setCurrentTime(long timeInMillis) {
    mCurrentTime.setText(TimeUtil.getTime(timeInMillis));
  }

  public void setEndTime(long timeInMillis) {
    mEndTime.setText(TimeUtil.getTime(timeInMillis));
  }

  public void setSeekBarProgress(int progress) {
    mSeekBar.setProgress(progress);
  }

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
        mHVAudioPlayer.doPlayPause();
      }
    });

    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) {
          return;
        }

        mHVAudioPlayer.updateCurrentTimeWhenDragging(progress);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDraggingSeekBar = true;
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        mIsDraggingSeekBar = false;
        mHVAudioPlayer.seekToStopTrackingTouchPosition(seekBar.getProgress());
      }
    });
  }
}
