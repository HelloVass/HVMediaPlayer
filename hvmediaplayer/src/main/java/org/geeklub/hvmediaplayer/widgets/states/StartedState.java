package org.geeklub.hvmediaplayer.widgets.states;

import org.geeklub.hvmediaplayer.widgets.factory.HVController;
import org.geeklub.hvmediaplayer.widgets.factory.HVPlayable;

/**
 * Created by HelloVass on 16/3/26.
 *
 * 具体状态类
 */
public class StartedState extends AbsMediaPlayerState {

  private static final String TAG = StartedState.class.getSimpleName();

  public StartedState(HVPlayable playable, HVController controller) {
    super(playable, controller);
  }

  @Override protected void prepareAsync() {

  }

  @Override protected void onPrepared() {

  }

  @Override protected void start() {

    mPlayable.resetPlayableTimer();
    mPlayable.startHVPlayable();

    mController.hidePlayButton();
    mController.showPauseButton();
  }

  @Override protected void pause() {
    mHVMediaPlayerContext.setPlayerState(mHVMediaPlayerContext.getPausedState());
    mHVMediaPlayerContext.pause();
  }
}
