package org.geeklub.hvmediaplayer.widgets.states;

import org.geeklub.hvmediaplayer.widgets.factory.HVController;
import org.geeklub.hvmediaplayer.widgets.factory.HVPlayable;

/**
 * Created by HelloVass on 16/3/30.
 *
 * 具体状态类
 */
public class PreparingState extends AbsMediaPlayerState {

  public PreparingState(HVPlayable playable, HVController controller) {
    super(playable, controller);
  }

  // TODO: 菊花转啊转
  @Override protected void prepareAsync() {
    mController.hide();
  }

  @Override protected void onPrepared() {
    mHVMediaPlayerContext.setPlayerState(mHVMediaPlayerContext.getPreparedState());
    mHVMediaPlayerContext.onPrepared();
  }

  @Override protected void start() {

  }

  @Override protected void pause() {

  }
}
