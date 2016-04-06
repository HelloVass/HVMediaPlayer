package org.geeklub.hvmediaplayer.widgets.factory;

/**
 * Created by HelloVass on 16/4/6.
 */
public interface AbsHVMediaPlayerFactory {

  /**
   * 创建播放组件
   *
   * @return 播放组件
   */
  HVPlayable createPlayable();

  /**
   * 创建操作栏
   *
   * @return 操作栏
   */
  HVController createController();
}
