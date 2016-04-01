package org.geeklub.hvmedia;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.HVMediaPlayer;
import org.geeklub.hvmediaplayer.widgets.playable_components.factory.HVAudioView;

/**
 * Created by HelloVass on 16/3/29.
 */
public class AudioActivity extends AppCompatActivity {

  private static final String TAG = AudioActivity.class.getSimpleName();

  // 测试音频地址
  private static final String TEST_AUDIO_URL =
      "http://audio-x.juju.la/f0ZhdfBuStBT1zwU3OUQUMDT9tQ=/lu6V5-Ma5yYJOGTlVqigQYywAJEk";

  // 测试音频的封面
  private static final String TEST_AUDIO_COVER_URL =
      "http://juju.inbbuy.cn/2016/03/29/8e4be12a5677a5cccecdfa6503534e0b.png@440w_2o";

  private HVMediaPlayer mHVMediaPlayer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_audio);

    mHVMediaPlayer = (HVMediaPlayer) findViewById(R.id.media_player);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    mHVMediaPlayer.buildAudioPlayer(TEST_AUDIO_URL, TEST_AUDIO_COVER_URL,
        new HVAudioView.CoverImageLoader() {
          @Override public void loadCoverImage(ImageView coverImageView, String coverUrl) {
            Glide.with(AudioActivity.this).load(coverUrl).centerCrop().into(coverImageView);
          }
        });

    mHVMediaPlayer.setHVMediaPlayerCallback(new HVMediaPlayer.HVMediaPlayerCallback() {
      @Override public void onEnterFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup.LayoutParams layoutParams = mHVMediaPlayer.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
      }

      @Override public void onExitScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewGroup.LayoutParams layoutParams = mHVMediaPlayer.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(AudioActivity.this, 320);
        layoutParams.height = DensityUtil.dip2px(AudioActivity.this, 180);
      }
    });
  }

  @Override protected void onPause() {
    super.onPause();
    mHVMediaPlayer.onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    mHVMediaPlayer.onResume();
  }
}
