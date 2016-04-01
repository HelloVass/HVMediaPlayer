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
public class PreparedState extends AbsMediaPlayerState {

  public PreparedState(IHVPlayable playable, HVMediaController controller) {
    super(playable, controller);
  }

  @Override protected void prepareAsync() {

  }

  @Override protected void onPrepared() {

    mController.setVisibility(View.VISIBLE); // 显示出操作栏

    mController.setCurrentTime(0);
    mController.setEndTime(mPlayable.getIHVPlayableDuration());

    mController.setSeekBarProgress(0);
    mController.setSeekBarSecondaryProgress(0);
  }

  @Override protected void start() {
    mHVMediaPlayerContext.setPlayerState(mHVMediaPlayerContext.getStartedState());
    mHVMediaPlayerContext.start();
  }

  @Override protected void pause() {

  }
}
