package org.geeklub.hvmediaplayer.widgets.player_states.concrete_state;

import android.view.View;
import org.geeklub.hvmediaplayer.widgets.controller.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;
import org.geeklub.hvmediaplayer.widgets.player_states.AbsMediaPlayerState;

/**
 * Created by HelloVass on 16/3/30.
 *
 * 具体状态类
 */
public class PreparingState extends AbsMediaPlayerState {

  public PreparingState(IHVPlayable playable, HVMediaController controller) {
    super(playable, controller);
  }

  // 菊花转啊转，暂时不写
  @Override protected void prepareAsync() {
    mController.setVisibility(View.GONE); // 隐藏操作栏
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
