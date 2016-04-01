package org.geeklub.hvmediaplayer.widgets.player_states;

import org.geeklub.hvmediaplayer.widgets.controller.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;

/**
 * Created by HelloVass on 16/3/30.
 *
 * Player 的状态抽象
 */
public abstract class AbsMediaPlayerState {

  protected HVMediaPlayerContext mHVMediaPlayerContext;

  protected IHVPlayable mPlayable;

  protected HVMediaController mController;

  public AbsMediaPlayerState(IHVPlayable playable, HVMediaController controller) {
    mPlayable = playable;
    mController = controller;
  }

  public void setHVMediaPlayerContext(HVMediaPlayerContext HVMediaPlayerContext) {
    mHVMediaPlayerContext = HVMediaPlayerContext;
  }

  /**
   * 进入 Preparing 状态
   */
  protected abstract void prepareAsync();

  /**
   * 进入 Prepared 状态
   */
  protected abstract void onPrepared();

  /**
   * 进入 Started 状态
   */
  protected abstract void start();

  /**
   * 进入 Paused 状态
   */
  protected abstract void pause();
}
