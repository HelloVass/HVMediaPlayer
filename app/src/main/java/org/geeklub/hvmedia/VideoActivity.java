package org.geeklub.hvmedia;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.HVMediaPlayer;

/**
 * Created by HelloVass on 16/3/21.
 */
public class VideoActivity extends AppCompatActivity {

  private static final String TAG = VideoActivity.class.getSimpleName();

  private static final String TEST_VIDEO_URL =
      "http://ws.acgvideo.com/2/6b/2544543-1.mp4?wsTime=1459540745&wsSecret2=7f02f5719773460b8c52d3b0ed9f7f17&oi=666177585&internal=1";

  private HVMediaPlayer mHVMediaPlayer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_video);

    mHVMediaPlayer = (HVMediaPlayer) findViewById(R.id.media_player);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    mHVMediaPlayer.buildVideoPlayer(TEST_VIDEO_URL);

    mHVMediaPlayer.setCallback(new HVMediaPlayer.Callback() {
      @Override public void onEnterFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup.LayoutParams layoutParams = mHVMediaPlayer.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
      }

      @Override public void onExitScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewGroup.LayoutParams layoutParams = mHVMediaPlayer.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(VideoActivity.this, 320);
        layoutParams.height = DensityUtil.dip2px(VideoActivity.this, 180);
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();
    mHVMediaPlayer.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
    mHVMediaPlayer.onPause();
  }
}
