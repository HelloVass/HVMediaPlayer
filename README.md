# HVMediaPlayer

## 介绍
一套针对**网络视频、音频**的播放器解决方案

## 音频播放器演示
![AudioPlayer Demo](./design/AudioPlayer Demo)

## 视频播放器演示
![VideoPlayer Demo](./design/VideoPlayer Demo)

## 使用

### 构造音频播放器

```java
public class AudioActivity extends AppCompatActivity {

  private static final String TAG = AudioActivity.class.getSimpleName();

  // 测试音频地址
  private static final String TEST_AUDIO_URL =
      "http://audio-x.juju.la/f0ZhdfBuStBT1zwU3OUQUMDT9tQ=/lu6V5-Ma5yYJOGTlVqigQYywAJEk";

  // 测试音频的封面
  private static final String TEST_AUDIO_COVER_URL =
      "http://juju.inbbuy.cn/2016/03/29/8e4be12a5677a5cccecdfa6503534e0b.png@440w_2o";

  private View mOpenAudioPlayerButton;

  private HVAudioPlayer mAudioPlayer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_audio);

    mOpenAudioPlayerButton = findViewById(R.id.btn_open_audio_player);

    // 构造播放器
    mAudioPlayer = new HVAudioPlayer.Builder(this).setAudioUrl(TEST_AUDIO_URL)
        .setCoverImageUrl(TEST_AUDIO_COVER_URL)
        .setImageLoader(new GlideImageLoaderFactory(this).createImageLoader())
        .setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            DensityUtil.dip2px(this, 220)))
        .build();

    addAudioPlayerToContentView(); // 将播放器添加到 ContentView 中

    mOpenAudioPlayerButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (!mAudioPlayer.isAddedToContent()) { // 播放器还未被添加到ContentView中
          return;
        }

        if (mAudioPlayer.isShown()) { // 播放器已经在显示了！
          return;
        }

        mAudioPlayer.reload(); // 重新加载视频

      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    mAudioPlayer.onDestroy(); // 销毁播放器
  }

  private void addAudioPlayerToContentView() {
    FrameLayout container =
        (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
    container.addView(mAudioPlayer);
  }
}
```

### 构造视频播放器

```java
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

    addVideoPlayerToContentView(); // 将播放器添加到 ContentView 中

    mOpenVideoPlayerButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (!mVideoPlayer.isAddedToContent()) { // 播放器还未被添加到ContentView
          return;
        }

        if (mVideoPlayer.isShown()) { // 播放器已经在显示了！
          return;
        }

        mVideoPlayer.reload(); // 重新加载播放器
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();

    if (mVideoPlayer.isAddedToContent()) {
      mVideoPlayer.restore(); // 恢复之前的播放进度
    }
  }

  @Override protected void onPause() {
    super.onPause();

    if (mVideoPlayer.isAddedToContent()) {
      mVideoPlayer.save(); // 保存播放进度
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    mVideoPlayer.onDestroy(); // 销毁播放器
  }

  private void addVideoPlayerToContentView() {
    FrameLayout container =
        (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
    container.addView(mVideoPlayer);
  }
}

```

## 缺点

目前太多了！

1.因为 `VideoView` 不支持在线音频播放，所以才模仿 `VideoView` 写了一个 `AudioView`

2.`VideoView` 会在 `Activity` 进入后台的时候将会销毁内部的 `mediaplayer`，重新回到前台的时候会重建 `mediaplayer`。（蛋疼的是，做这两个操作的时候，也不给个回调）所以，只好使用 `Activity` 的生命周期函数来实现保存播放进度的功能，感觉很不优雅！

3. 播放器的交互待优化...
...




