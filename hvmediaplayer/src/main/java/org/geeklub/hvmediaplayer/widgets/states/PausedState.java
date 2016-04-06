package org.geeklub.hvmediaplayer.widgets.states;

import org.geeklub.hvmediaplayer.widgets.factory.HVController;
import org.geeklub.hvmediaplayer.widgets.factory.HVPlayable;

/**
 * Created by HelloVass on 16/3/26.
 *
 * 具体状态类
 */
public class PausedState extends AbsMediaPlayerState {

  private static final String TAG = PausedState.class.getSimpleName();

  public PausedState(HVPlayable playable, HVController controller) {
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

    mController.hidePauseButton();
    mController.showPlayButton();

    mPlayable.pauseHVPlayable();
  }
}
