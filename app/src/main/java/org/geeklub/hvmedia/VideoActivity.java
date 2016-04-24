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

  private HVVideoPlayer mVideoPlayer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_video);

    mOpenVideoPlayerButton = findViewById(R.id.btn_open_video_player);

    mVideoPlayer = new HVVideoPlayer.Builder(this).setVideoUrl(TEST_VIDEO_URL)
        .setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            DensityUtil.dip2px(this, 220)))
        .build();
    addVideoPlayerToContentView();

    mOpenVideoPlayerButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (!mVideoPlayer.isAddedToContent()) { // 播放器还未被添加到Content
          return;
        }

        if (mVideoPlayer.isShown()) { // 播放器已经在显示了！
          return;
        }

        mVideoPlayer.reload();
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();

    if (mVideoPlayer.isAddedToContent()) {
      mVideoPlayer.restore();
    }
  }

  @Override protected void onPause() {
    super.onPause();

    if (mVideoPlayer.isAddedToContent()) {
      mVideoPlayer.save();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    mVideoPlayer.onDestroy();
  }

  private void addVideoPlayerToContentView() {
    FrameLayout container =
        (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
    container.addView(mVideoPlayer);
  }
}
