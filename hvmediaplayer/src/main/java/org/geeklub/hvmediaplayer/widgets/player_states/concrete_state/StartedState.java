package org.geeklub.hvmediaplayer.widgets.player_states.concrete_state;

import org.geeklub.hvmediaplayer.widgets.controller.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;
import org.geeklub.hvmediaplayer.widgets.player_states.AbsMediaPlayerState;

/**
 * Created by HelloVass on 16/3/26.
 *
 * 具体状态类
 */
public class StartedState extends AbsMediaPlayerState {

  private static final String TAG = StartedState.class.getSimpleName();

  public StartedState(IHVPlayable playable, HVMediaController controller) {
    super(playable, controller);
  }

  @Override protected void prepareAsync() {

  }

  @Override protected void onPrepared() {

  }

  @Override protected void start() {

    mPlayable.resetUpdatePlayableTimer();
    mPlayable.startIHVPlayable();

    mController.showOrHidePlayButton(false);
    mController.showOrHidePauseButton(true);
  }

  @Override protected void pause() {
    mHVMediaPlayerContext.setPlayerState(mHVMediaPlayerContext.getPausedState());
    mHVMediaPlayerContext.pause();
  }
}
