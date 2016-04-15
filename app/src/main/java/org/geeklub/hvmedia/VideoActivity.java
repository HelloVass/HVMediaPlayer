package org.geeklub.hvmedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.geeklub.hvmediaplayer.utils.DensityUtil;
import org.geeklub.hvmediaplayer.widgets.video.HVVideoPlayer;

/**
 * Created by HelloVass on 16/3/21.
 */
public class VideoActivity extends AppCompatActivity {

  private static final String TAG = VideoActivity.class.getSimpleName();

  private static final String TEST_VIDEO_URL =
      "http://video-x.juju.la/UAA-4hndfVc5V6DJX0EvslAUBBI=/lgoChlyXCZZkaVC0rZ1pBEX-8gvz";

  private View mOpenVideoPlayerButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_video);

    mOpenVideoPlayerButton = findViewById(R.id.btn_open_video_player);

    mOpenVideoPlayerButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        addVideoPlayerToContentView();
      }
    });
  }

  private void addVideoPlayerToContentView() {

    FrameLayout container =
        (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);

    HVVideoPlayer videoPlayer = new HVVideoPlayer.Builder(this).setVideoUrl(TEST_VIDEO_URL)
        .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            DensityUtil.dip2px(this, 220)))
        .build();

    container.addView(videoPlayer);
  }
}
