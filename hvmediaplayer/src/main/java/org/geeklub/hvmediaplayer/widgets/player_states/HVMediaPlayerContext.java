package org.geeklub.hvmediaplayer.widgets.player_states;

import org.geeklub.hvmediaplayer.widgets.controller.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.playable_components.IHVPlayable;
import org.geeklub.hvmediaplayer.widgets.player_states.concrete_state.PausedState;
import org.geeklub.hvmediaplayer.widgets.player_states.concrete_state.PreparedState;
import org.geeklub.hvmediaplayer.widgets.player_states.concrete_state.PreparingState;
import org.geeklub.hvmediaplayer.widgets.player_states.concrete_state.StartedState;

/**
 * Created by HelloVass on 16/3/30.
 *
 * HVMediaPlayer 所关心的环境类
 */
public class HVMediaPlayerContext {

  private PreparingState mPreparingState;

  private PreparedState mPreparedState;

  private StartedState mStartedState;

  private PausedState mPausedState;

  private AbsMediaPlayerState mPlayerState;

  public HVMediaPlayerContext(IHVPlayable playable, HVMediaController controller) {
    mPreparingState = new PreparingState(playable, controller);
    mPreparedState = new PreparedState(playable, controller);
    mStartedState = new StartedState(playable, controller);
    mPausedState = new PausedState(playable, controller);
  }

  public PreparedState getPreparedState() {
    return mPreparedState;
  }

  public StartedState getStartedState() {
    return mStartedState;
  }

  public PreparingState getPreparingState() {
    return mPreparingState;
  }

  public PausedState getPausedState() {
    return mPausedState;
  }

  public void setPlayerState(AbsMediaPlayerState playerState) {
    mPlayerState = playerState;
    mPlayerState.setHVMediaPlayerContext(this);
  }

  public void prepareAsync() {
    mPlayerState.prepareAsync();
  }

  public void onPrepared() {
    mPlayerState.onPrepared();
  }

  public void start() {
    mPlayerState.start();
  }

  public void pause() {
    mPlayerState.pause();
  }
}
