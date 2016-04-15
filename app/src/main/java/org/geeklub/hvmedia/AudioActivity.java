package org.geeklub.hvmedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.geeklub.hvmedia.imageloader.GlideImageLoaderFactory;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.audio.HVAudioPlayer;

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

  private View mOpenAudioPlayerButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_audio);

    mOpenAudioPlayerButton = findViewById(R.id.btn_open_audio_player);

    mOpenAudioPlayerButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        addAudioPlayerToContentView();
      }
    });
  }

  private void addAudioPlayerToContentView() {

    FrameLayout container =
        (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);

    HVAudioPlayer audioPlayer = new HVAudioPlayer.Builder(this).setAudioUrl(TEST_AUDIO_URL)
        .setCoverImageUrl(TEST_AUDIO_COVER_URL)
        .setImageLoader(new GlideImageLoaderFactory(this).createImageLoader())
        .build();

    ViewGroup.LayoutParams audioPlayerLayoutParams =
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            DensityUtil.dip2px(this, 220));

    container.addView(audioPlayer, audioPlayerLayoutParams);
  }
}
