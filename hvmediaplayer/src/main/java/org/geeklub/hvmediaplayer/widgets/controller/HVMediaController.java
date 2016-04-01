package org.geeklub.hvmediaplayer.widgets.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.geeklub.hvmediaplayer.R;
import org.geeklub.hvmediaplayer.utils.TimeUtil;

/**
 * Created by HelloVass on 16/3/24.
 *
 * 操作栏
 */
public class HVMediaController extends FrameLayout {

  private static final String TAG = HVMediaController.class.getSimpleName();

  private View mPlayButton;

  private View mPauseButton;

  private TextView mCurrentTime;

  private TextView mEndTime;

  private SeekBar mSeekBar;

  private View mShrinkButton;

  private View mExpandButton;

  private HVMediaControllerCallback mHVMediaControllerCallback;

  private boolean mIsEnterFullScreen = false;

  private boolean mIsDraggingSeekBar = false;

  public HVMediaController(Context context) {
    this(context, null);
  }

  public HVMediaController(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HVMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void setHVMediaControllerCallback(HVMediaControllerCallback callback) {
    mHVMediaControllerCallback = callback;
  }

  public boolean isEnterFullScreen() {
    return mIsEnterFullScreen;
  }

  public void setIsEnterFullScreen(boolean isEnterFullScreen) {
    mIsEnterFullScreen = isEnterFullScreen;
  }

  public boolean isDraggingSeekBar() {
    return mIsDraggingSeekBar;
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

  public void showOrHidePlayButton(boolean ifShow) {
    mPlayButton.setVisibility(ifShow ? VISIBLE : GONE);
  }

  public void showOrHidePauseButton(boolean ifShow) {
    mPauseButton.setVisibility(ifShow ? VISIBLE : GONE);
  }

  public void showOrHideShrinkButton(boolean ifShow) {
    mShrinkButton.setVisibility(ifShow ? VISIBLE : GONE);
  }

  public void showOrHideExpandButton(boolean ifShow) {
    mExpandButton.setVisibility(ifShow ? VISIBLE : GONE);
  }

  private void init() {

    View.inflate(getContext(), R.layout.layout_media_controller, this);

    mPlayButton = findViewById(R.id.iv_play);
    mPauseButton = findViewById(R.id.iv_pause);
    mCurrentTime = (TextView) findViewById(R.id.tv_current);
    mSeekBar = (SeekBar) findViewById(R.id.sb_progress);
    mEndTime = (TextView) findViewById(R.id.tv_end);
    mShrinkButton = findViewById(R.id.iv_shrink);
    mExpandButton = findViewById(R.id.iv_expand);

    setUpCallbacks();
  }

  private void setUpCallbacks() {

    mPauseButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mHVMediaControllerCallback != null) {
          mHVMediaControllerCallback.pause();
        }
      }
    });

    mPlayButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mHVMediaControllerCallback != null) {
          mHVMediaControllerCallback.start();
        }
      }
    });

    mShrinkButton.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View v) {

        if (mHVMediaControllerCallback != null && mIsEnterFullScreen) {

          showOrHideShrinkButton(false); // 隐藏“退出全屏”按钮
          showOrHideExpandButton(true); // 显示“进入全屏”按钮
          mHVMediaControllerCallback.shrink();
        }
      }
    });

    mExpandButton.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View v) {

        if (mHVMediaControllerCallback != null && !mIsEnterFullScreen) {

          showOrHideExpandButton(false); // 隐藏“进入全屏”按钮
          showOrHideShrinkButton(true); // 显示“退出全屏”按钮
          mHVMediaControllerCallback.expand();
        }
      }
    });

    // 设置 SeekBar 回调监听事件
    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) {
          return;
        }

        mHVMediaControllerCallback.updateCurrentTimeWhenDragging(progress);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDraggingSeekBar = true;
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

        mIsDraggingSeekBar = false;

        if (mHVMediaControllerCallback != null) {
          mHVMediaControllerCallback.onProgressChanged(seekBar.getProgress());
        }
      }
    });
  }

  public interface HVMediaControllerCallback {

    void start();

    void pause();

    void shrink();

    void expand();

    void updateCurrentTimeWhenDragging(int progress);

    void onProgressChanged(int progress);
  }
}
