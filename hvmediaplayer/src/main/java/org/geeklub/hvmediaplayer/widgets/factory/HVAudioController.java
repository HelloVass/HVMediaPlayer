package org.geeklub.hvmediaplayer.widgets.factory;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.geeklub.hvmediaplayer.R;
import org.geeklub.hvmediaplayer.utils.TimeUtil;

/**
 * Created by HelloVass on 16/4/6.
 */
public class HVAudioController extends FrameLayout implements HVController {

  private View mPlayButton;

  private View mPauseButton;

  private TextView mCurrentTime;

  private TextView mEndTime;

  private SeekBar mSeekBar;

  private Callback mCallback;

  private boolean mIsDraggingSeekBar = false;

  public HVAudioController(Context context) {
    super(context);
    init();
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  @Override public boolean isDraggingSeekBar() {
    return mIsDraggingSeekBar;
  }

  @Override public void setCurrentTime(long timeInMillis) {
    mCurrentTime.setText(TimeUtil.getTime(timeInMillis));
  }

  @Override public void setEndTime(long timeInMillis) {
    mEndTime.setText(TimeUtil.getTime(timeInMillis));
  }

  @Override public void setSeekBarProgress(int progress) {
    mSeekBar.setProgress(progress);
  }

  @Override public void setSeekBarSecondaryProgress(int secondaryProgress) {
    mSeekBar.setSecondaryProgress(secondaryProgress);
  }

  @Override public void hide() {
    setVisibility(GONE);
  }

  @Override public void show() {
    setVisibility(VISIBLE);
  }

  @Override public void showPlayButton() {
    mPlayButton.setVisibility(VISIBLE);
  }

  @Override public void hidePlayButton() {
    mPlayButton.setVisibility(GONE);
  }

  @Override public void showPauseButton() {
    mPauseButton.setVisibility(VISIBLE);
  }

  @Override public void hidePauseButton() {
    mPauseButton.setVisibility(GONE);
  }

  private void init() {

    View.inflate(getContext(), R.layout.layout_audio_controller, this);

    mPlayButton = findViewById(R.id.iv_play);
    mPauseButton = findViewById(R.id.iv_pause);
    mCurrentTime = (TextView) findViewById(R.id.tv_current);
    mSeekBar = (SeekBar) findViewById(R.id.sb_progress);
    mEndTime = (TextView) findViewById(R.id.tv_end);

    setUpCallbacks();
  }

  private void setUpCallbacks() {

    mPlayButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mCallback != null) {
          mCallback.start();
        }
      }
    });

    mPauseButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mCallback != null) {
          mCallback.pause();
        }
      }
    });

    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) {
          return;
        }

        if (mCallback != null) {
          mCallback.updateCurrentTimeWhenDragging(progress);
        }
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDraggingSeekBar = true;
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

        mIsDraggingSeekBar = false;

        if (mCallback != null) {
          mCallback.onProgressChanged(seekBar.getProgress());
        }
      }
    });
  }

  public interface Callback {

    void start();

    void pause();

    void updateCurrentTimeWhenDragging(int progress);

    void onProgressChanged(int progress);
  }
}
