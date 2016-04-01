package org.geeklub.hvmediaplayer.widgets.player_states.concrete_state;

import org.geeklub.hvmediaplayer.widgets.controller.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;
import org.geeklub.hvmediaplayer.widgets.player_states.AbsMediaPlayerState;

/**
 * Created by HelloVass on 16/3/26.
 *
 * 具体状态类
 */
public class PausedState extends AbsMediaPlayerState {

  private static final String TAG = PausedState.class.getSimpleName();

  public PausedState(IHVPlayable playable, HVMediaController controller) {
    super(playable, controller);
  }

  @Override protected void prepareAsync() {

  }

  @Override protected void onPrepared() {

  }

  @Override protected void start() {
    mHVMediaPlayerContext.setPlayerState(mHVMediaPlayerContext.getStartedState());
    mHVMediaPlayerContext.start();
  }

  @Override protected void pause() {

    mController.showOrHidePauseButton(false);
    mController.showOrHidePlayButton(true);

    mPlayable.pauseIHVPlayable();
  }
}
