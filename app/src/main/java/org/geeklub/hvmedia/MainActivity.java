package org.geeklub.hvmedia;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.HVMediaPlayer;

/**
 * Created by HelloVass on 16/3/21.
 */
public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static final String VIDEO_TEST_URL =
      "http://112.17.2.49/edge.v.iask.com/130481345.mp4?KID=sina,viask&Expires=1459094400&ssig=REghwIsPAS&wshc_tag=0&wsts_tag=56f663e1&wsid_tag=27b57e8c&wsiphost=ipdbm";

  private HVMediaPlayer mHVMediaPlayer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mHVMediaPlayer = (HVMediaPlayer) findViewById(R.id.media_player);

    mHVMediaPlayer.setHVMediaPlayerCallback(new HVMediaPlayer.HVMediaPlayerCallback() {
      @Override public void onEnterFullScreen() {
        Log.i(TAG, "enter full screen");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup.LayoutParams layoutParams = mHVMediaPlayer.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
      }

      @Override public void onExitScreen() {
        Log.i(TAG, "exit full screen");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewGroup.LayoutParams layoutParams = mHVMediaPlayer.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = DensityUtil.dip2px(MainActivity.this, 240);
      }
    });
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mHVMediaPlayer.loadAndPlay(VIDEO_TEST_URL);
  }
}
