package org.geeklub.hvmediaplayer.widgets.states;

import org.geeklub.hvmediaplayer.widgets.factory.HVController;
import org.geeklub.hvmediaplayer.widgets.factory.HVPlayable;

/**
 * Created by HelloVass on 16/3/30.
 *
 * 具体状态类
 */
public class PreparedState extends AbsMediaPlayerState {

  public PreparedState(HVPlayable playable, HVController controller) {
    super(playable, controller);
  }

  @Override protected void prepareAsync() {

  }

  @Override protected void onPrepared() {

    mController.show();

    mController.setCurrentTime(0);
    mController.setEndTime(mPlayable.getHVPlayableDuration());

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
