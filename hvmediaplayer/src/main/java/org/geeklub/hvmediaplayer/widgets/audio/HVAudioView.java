package org.geeklub.hvmediaplayer.widgets.audio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.io.IOException;
import java.util.Map;
import org.geeklub.hvmediaplayer.imageloader.ImageLoader;

/**
 * Created by HelloVass on 16/3/27.
 */
public class HVAudioView extends RelativeLayout {

  private MediaPlayer mMediaPlayer;

  private Mediator mHVAudioPlayer;

  private ImageLoader mImageLoader;

  private ImageView mCoverImageView;

  private UpdatePlayableTimer mUpdatePlayableTimer;

  private Uri mUri;

  private String mCoverImageUrl;

  private Map<String, String> mHeaders;

  private static final int STATE_ERROR = -1; // 错误状态

  private static final int STATE_IDLE = 0; // 闲置状态

  private static final int STATE_PREPARING = 1; // 准备中状态

  private static final int STATE_PREPARED = 2; // 准备完毕状态

  private static final int STATE_PLAYING = 3; // 播放中状态

  private static final int STATE_PAUSED = 4; // 暂停状态

  private static final int STATE_PLAYBACK_COMPLETED = 5; // 播放完成状态

  private int mCurrentState = STATE_IDLE; // 当前状态默认为闲置状态

  private int mTargetState = STATE_IDLE; // 目标状态默认也为闲置状态

  private MediaPlayer.OnPreparedListener mOnPreparedListener;

  private MediaPlayer.OnCompletionListener mOnCompletionListener;

  private MediaPlayer.OnErrorListener mOnErrorListener;

  private int mCurrentBufferPercentage; // 缓冲进度

  private int mSeekWhenPrepared; // 在”preparing“的时候记录“seek”的位置

  public HVAudioView(Context context) {
    super(context);
    init();
  }

  public void setHVAudioPlayer(Mediator HVAudioPlayer) {
    mHVAudioPlayer = HVAudioPlayer;
  }

  public void setImageLoader(ImageLoader imageLoader) {
    mImageLoader = imageLoader;
  }

  public void setAudioPath(String path) {
    setAudioURI(Uri.parse(path));
  }

  public void setAudioURI(Uri uri) {
    setAudioURI(uri, null);
  }

  public void setAudioURI(Uri uri, Map<String, String> headers) {
    mUri = uri;
    mHeaders = headers;
    mSeekWhenPrepared = 0;
    openAudio();
  }

  public void setCoverImagePath(String coverImageUrl) {
    mCoverImageUrl = coverImageUrl;
    loadCoverImage();
  }

  public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
    mOnPreparedListener = onPreparedListener;
  }

  public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
    mOnCompletionListener = onCompletionListener;
  }

  public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
    mOnErrorListener = onErrorListener;
  }

  public void start() {
    if (isInPlaybackState()) {
      mMediaPlayer.start();
      mCurrentState = STATE_PLAYING;
    }
    mTargetState = STATE_PLAYING;
  }

  public void pause() {
    if (isInPlaybackState()) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
        mCurrentState = STATE_PAUSED;
      }
    }
    mTargetState = STATE_PAUSED;
  }

  public boolean isPlaying() {
    return isInPlaybackState() && mMediaPlayer.isPlaying();
  }

  public int getDuration() {
    if (isInPlaybackState()) {
      return mMediaPlayer.getDuration();
    }
    return -1;
  }

  public int getCurrentPosition() {
    if (isInPlaybackState()) {
      return mMediaPlayer.getCurrentPosition();
    }
    return 0;
  }

  public void seekTo(int timeInMillis) {
    if (isInPlaybackState()) {
      mMediaPlayer.seekTo(timeInMillis);
      mSeekWhenPrepared = 0;
    } else {
      mSeekWhenPrepared = timeInMillis;
    }
  }

  public void stopPlayback() {
    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
      mMediaPlayer.release();
      mMediaPlayer = null;
      mCurrentState = STATE_IDLE;
      mTargetState = STATE_IDLE;
    }
  }

  public void resetTimer() {
    mUpdatePlayableTimer = new UpdatePlayableTimer(getDuration(), 250L);
    mUpdatePlayableTimer.start();
  }

  public void stopTimer() {
    mUpdatePlayableTimer.cancel();
    mUpdatePlayableTimer = null;
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    release(true);
  }

  private void init() {

    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();

    mCurrentState = STATE_IDLE;
    mTargetState = STATE_IDLE;

    addCoverImageView();
    setUpCallbacks();
  }

  private void addCoverImageView() {
    mCoverImageView = new ImageView(getContext());
    LayoutParams coverLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mCoverImageView.setLayoutParams(coverLayoutParams);
    mCoverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    addView(mCoverImageView);
  }

  private void setUpCallbacks() {

    setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override public void onPrepared(MediaPlayer mp) {
        mHVAudioPlayer.onPrepared(mp);
      }
    });

    setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mp) {
        mHVAudioPlayer.onCompletion(mp);
      }
    });

    setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mHVAudioPlayer != null) {
          mHVAudioPlayer.onError(mp, what, extra);
          return true;
        }
        return false;
      }
    });
  }

  private void openAudio() {

    if (mUri == null) {
      return;
    }

    release(false);

    try {
      mMediaPlayer = new MediaPlayer();

      mMediaPlayer.setOnPreparedListener(new InnerOnPreparedListener());
      mMediaPlayer.setOnCompletionListener(new InnerOnCompletionListener());
      mMediaPlayer.setOnErrorListener(new InnerOnErrorListener());
      mMediaPlayer.setOnBufferingUpdateListener(new InnerBufferingUpdateListener());

      mCurrentBufferPercentage = 0;

      mMediaPlayer.setDataSource(getContext(), mUri, mHeaders);
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mMediaPlayer.setScreenOnWhilePlaying(true);
      mMediaPlayer.prepareAsync();

      mCurrentState = STATE_PREPARING;
    } catch (IOException | IllegalArgumentException e) {
      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;
      mOnErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
    }
  }

  private void loadCoverImage() {

    if (mImageLoader == null) {
      return;
    }

    if (mCoverImageView == null) {
      return;
    }

    if (mCoverImageUrl == null || TextUtils.isEmpty(mCoverImageUrl.trim())) {
      return;
    }

    mImageLoader.displayImage(mCoverImageView, mCoverImageUrl);
  }

  private int getBufferPercentage() {
    if (mMediaPlayer != null) {
      return mCurrentBufferPercentage;
    }
    return 0;
  }

  private void release(boolean cleatTargetState) {

    if (mMediaPlayer != null) {

      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
      mCurrentState = STATE_IDLE;

      if (cleatTargetState) {
        mTargetState = STATE_IDLE;
      }
    }
  }

  private boolean isInPlaybackState() {
    return (mMediaPlayer != null
        && mCurrentState != STATE_ERROR
        && mCurrentState != STATE_IDLE
        && mCurrentState != STATE_PREPARING);
  }

  private class InnerOnPreparedListener implements MediaPlayer.OnPreparedListener {

    @Override public void onPrepared(MediaPlayer mp) {

      mCurrentState = STATE_PREPARED;

      if (mOnPreparedListener != null) {
        mOnPreparedListener.onPrepared(mMediaPlayer);
      }

      int seekToPosition = mSeekWhenPrepared;

      if (seekToPosition != 0) {
        seekTo(seekToPosition);
      }

      if (mTargetState == STATE_PLAYING) {
        start();
      }
    }
  }

  private class InnerOnCompletionListener implements MediaPlayer.OnCompletionListener {

    @Override public void onCompletion(MediaPlayer mp) {

      mCurrentState = STATE_PLAYBACK_COMPLETED;
      mTargetState = STATE_PLAYBACK_COMPLETED;

      if (mOnCompletionListener != null) {
        mOnCompletionListener.onCompletion(mMediaPlayer);
      }
    }
  }

  private class InnerOnErrorListener implements MediaPlayer.OnErrorListener {

    @Override public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {

      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;

      if (mOnErrorListener != null) {
        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
          return true;
        }
      }

      if (getWindowToken() != null) {

        int messageResId;

        if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
          messageResId = android.R.string.VideoView_error_text_invalid_progressive_playback;
        } else {
          messageResId = android.R.string.VideoView_error_text_unknown;
        }

        new AlertDialog.Builder(getContext()).setMessage(messageResId)
            .setPositiveButton(android.R.string.VideoView_error_button,
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int whichButton) {
                    if (mOnCompletionListener != null) {
                      mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                  }
                })
            .setCancelable(false)
            .show();
      }

      return true;
    }
  }

  private class InnerBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
    @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
      mCurrentBufferPercentage = percent;
    }
  }

  private class UpdatePlayableTimer extends CountDownTimer {

    public UpdatePlayableTimer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override public void onTick(long millisUntilFinished) {

      if (isPlaying()) {
        float percent = (float) getCurrentPosition() / (float) getDuration();
        mHVAudioPlayer.updateCurrentTimeWhenPlaying((int) (percent * 100), getBufferPercentage());
      }
    }

    @Override public void onFinish() {

    }
  }
}
