package org.geeklub.hvmediaplayer.widgets.playable_components.factory;

import android.content.Context;

/**
 * Created by HelloVass on 16/3/29.
 */
public class HVMediaPlayerFactory {

  /**
   * 静态工厂构建 VideoView
   *
   * @param context 上下文
   * @param mediaUrl 媒体的地址
   * @return HVVideoView
   */
  public static HVVideoView createHVVideoView(Context context, String mediaUrl) {
    HVVideoView videoView = new HVVideoView(context);
    videoView.setVideoPath(mediaUrl);
    return videoView;
  }

  /**
   * 静态工厂构建 HVAudioView
   *
   * @param context 上下文
   * @param mediaUrl 媒体的地址
   * @param coverUrl 封面的地址
   * @return HVAudioView
   */
  public static HVAudioView createHVAudioView(Context context, String mediaUrl, String coverUrl,
      HVAudioView.CoverImageLoader coverImageLoader) {
    HVAudioView audioView = new HVAudioView(context);
    audioView.setAudioPath(mediaUrl);
    audioView.setCoverUrl(coverUrl);
    audioView.setCoverImageLoader(coverImageLoader);
    return audioView;
  }
}
