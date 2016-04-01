package org.geeklub.hvmediaplayer.widgets.playable_components.factory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.io.IOException;
import java.util.Map;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;

/**
 * Created by HelloVass on 16/3/27.
 */
public class HVAudioView extends RelativeLayout implements IHVPlayable {

  private ImageView mCoverImageView;

  private MediaPlayer mMediaPlayer;

  private IHVPlayableCallback mIHVPlayableCallback;

  private CoverImageLoader mCoverImageLoader;

  private UpdatePlayableTimer mUpdatePlayableTimer;

  private Uri mUri;

  private String mCoverUrl;

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

  public void setCoverUrl(String coverUrl) {
    mCoverUrl = coverUrl;
  }

  public void setCoverImageLoader(CoverImageLoader coverImageLoader) {
    mCoverImageLoader = coverImageLoader;
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

  @Override public int getIHVPlayableCurrentPosition() {
    if (isInPlaybackState()) {
      return mMediaPlayer.getCurrentPosition();
    }
    return 0;
  }

  @Override public int getIHVPlayableDuration() {
    if (isInPlaybackState()) {
      return mMediaPlayer.getDuration();
    }
    return -1;
  }

  @Override public boolean isIHVPlayablePlaying() {
    return isInPlaybackState() && mMediaPlayer.isPlaying();
  }

  @Override public int getIHVPlayableBufferPercentage() {
    return getBufferPercentage();
  }

  @Override public void IHVPlayableSeekTo(int timeInMillis) {
    if (isInPlaybackState()) {
      mMediaPlayer.seekTo(timeInMillis);
      mSeekWhenPrepared = 0;
    } else {
      mSeekWhenPrepared = timeInMillis;
    }
  }

  @Override public void setIHVPlayableOnTouchListener(OnTouchListener onTouchListener) {
    setOnTouchListener(onTouchListener);
  }

  @Override public void setIHVPlayableCallback(IHVPlayableCallback callback) {
    mIHVPlayableCallback = callback;
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

  @Override public void resetUpdatePlayableTimer() {
    mUpdatePlayableTimer = new UpdatePlayableTimer(getIHVPlayableDuration(), 250);
    mUpdatePlayableTimer.start();
  }

  @Override public void stopUpdatePlayableTimer() {
    mUpdatePlayableTimer.cancel();
    mUpdatePlayableTimer = null;
  }

  @Override public void pauseIHVPlayable() {
    if (isInPlaybackState()) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
        mCurrentState = STATE_PAUSED;
      }
    }
    mTargetState = STATE_PAUSED;
  }

  @Override public void startIHVPlayable() {
    if (isInPlaybackState()) {
      mMediaPlayer.start();
      mCurrentState = STATE_PLAYING;
    }
    mTargetState = STATE_PLAYING;
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    release(true); // 释放 MediaPlayer
  }

  private void init() {
    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();
    mCurrentState = STATE_IDLE;
    mTargetState = STATE_IDLE;

    mCoverImageView = new ImageView(getContext());

    LayoutParams coverLayoutParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mCoverImageView.setLayoutParams(coverLayoutParams);
    mCoverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

    addView(mCoverImageView);

    setUpCallbacks();
  }

  private void setUpCallbacks() {

    setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override public void onPrepared(MediaPlayer mp) {
        if (mIHVPlayableCallback != null) {
          mIHVPlayableCallback.onPrepared();
        }
      }
    });

    setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mp) {
        if (mIHVPlayableCallback != null) {
          mIHVPlayableCallback.onCompletion(mp);
        }
      }
    });

    setOnErrorListener(new MediaPlayer.OnErrorListener() {
      @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mIHVPlayableCallback != null) {
          mIHVPlayableCallback.onError(mp);
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
    if (mCoverUrl == null) {
      return;
    }

    if (mCoverImageLoader != null) {
      mCoverImageLoader.loadCoverImage(mCoverImageView, mCoverUrl);
    }
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

  private int getBufferPercentage() {
    if (mMediaPlayer != null) {
      return mCurrentBufferPercentage;
    }
    return 0;
  }

  /**
   * 缓冲完成后的回调
   */
  private class InnerOnPreparedListener implements MediaPlayer.OnPreparedListener {

    @Override public void onPrepared(MediaPlayer mp) {

      mCurrentState = STATE_PREPARED;

      if (mOnPreparedListener != null) {
        mOnPreparedListener.onPrepared(mMediaPlayer);
      }

      int seekToPosition = mSeekWhenPrepared;

      if (seekToPosition != 0) {
        IHVPlayableSeekTo(seekToPosition);
      }

      if (mTargetState == STATE_PLAYING) {
        startIHVPlayable();
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

  public interface CoverImageLoader {
    void loadCoverImage(ImageView coverImageView, String coverUrl);
  }

  /**
   * 定时更新“可播放组件”播放进度任务
   */
  private class UpdatePlayableTimer extends CountDownTimer {

    public UpdatePlayableTimer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override public void onTick(long millisUntilFinished) {

      if (mIHVPlayableCallback != null && isIHVPlayablePlaying()) {
        float percent = (float) getIHVPlayableCurrentPosition() / (float) getIHVPlayableDuration();
        mIHVPlayableCallback.onProgressChanged((int) (percent * 100),
            getIHVPlayableBufferPercentage());
      }
    }

    @Override public void onFinish() {

    }
  }
}
