package org.geeklub.hvmediaplayer.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.geeklub.hvmediaplayer.R;
import org.geeklub.hvmediaplayer.states.FinishedState;
import org.geeklub.hvmediaplayer.states.PauseState;
import org.geeklub.hvmediaplayer.states.PreparedState;
import org.geeklub.hvmediaplayer.states.StartState;
import org.geeklub.hvmediaplayer.utils.TimeUtil;

/**
 * Created by HelloVass on 16/3/24.
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

  private HVMediaPlayer.IHVMediaPlayer mIHVMediaPlayer;

  private boolean mIsEnterFullScreen = false;

  private boolean mIsDraggingSeekBar = false;

  private boolean mIsFromUser = false;

  private State mPreparedState = new PreparedState();

  private State mStartState = new StartState();

  private State mPauseState = new PauseState();

  private State mFinishedState = new FinishedState();

  private State mState = mPreparedState;

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

  public void setState(State state) {
    mState = state;
  }

  public State getState() {
    return mState;
  }

  public State getPreparedState() {
    return mPreparedState;
  }

  public State getStartState() {
    return mStartState;
  }

  public State getPauseState() {
    return mPauseState;
  }

  public State getFinishedState() {
    return mFinishedState;
  }

  public void setHVMediaControllerCallback(HVMediaControllerCallback callback) {
    mHVMediaControllerCallback = callback;
  }

  public void setIHVMediaPlayer(HVMediaPlayer.IHVMediaPlayer IHVMediaPlayer) {
    mIHVMediaPlayer = IHVMediaPlayer;
  }

  public void setCurrentTime(long timeInMillis) {
    mCurrentTime.setText(TimeUtil.getTime(timeInMillis));
  }

  public boolean isDraggingSeekBar() {
    return mIsDraggingSeekBar;
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
        if (mHVMediaControllerCallback != null && mIHVMediaPlayer.isPlaying()) {
          mHVMediaControllerCallback.pause();
        }
      }
    });

    mPlayButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (mHVMediaControllerCallback != null && !mIHVMediaPlayer.isPlaying()) {
          mHVMediaControllerCallback.start();
        }
      }
    });

    mShrinkButton.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View v) {

        if (mHVMediaControllerCallback != null && mIsEnterFullScreen) {

          showOrHideShrinkButton(false);// 隐藏“退出全屏”按钮
          showOrHideExpandButton(true);// 显示“进入全屏”按钮
          mHVMediaControllerCallback.shrink();

          mIsEnterFullScreen = false;
        }
      }
    });

    mExpandButton.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View v) {

        if (mHVMediaControllerCallback != null && !mIsEnterFullScreen) {

          showOrHideExpandButton(false); // 隐藏“进入全屏”按钮
          showOrHideShrinkButton(true); // 显示“退出全屏”按钮
          mHVMediaControllerCallback.expand();

          mIsEnterFullScreen = true;
        }
      }
    });

    // 设置 SeekBar 回调监听事件
    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mIsFromUser = fromUser;
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDraggingSeekBar = true;
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

        mIsDraggingSeekBar = false;

        if (mHVMediaControllerCallback != null && mIsFromUser) {
          mHVMediaControllerCallback.onProgressChanged(seekBar.getProgress());
        }
      }
    });
  }

  public interface State {

    void preparedState(HVVideoView videoView, HVMediaController controller);

    void startState(HVVideoView videoView, HVMediaController controller);

    void pauseState(HVVideoView videoView, HVMediaController controller);

    void finishedState(HVVideoView videoView, HVMediaController controller);
  }

  public interface HVMediaControllerCallback {

    void start();

    void pause();

    void shrink();

    void expand();

    void onProgressChanged(int progress);
  }
}
