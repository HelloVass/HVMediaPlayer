package org.geeklub.hvmediaplayer.widgets.video.support_commands;

import org.geeklub.hvmediaplayer.widgets.video.HVVideoView;

/**
 * Created by HelloVass on 16/4/13.
 */
public class PauseCommand implements Command {

  private HVVideoView mHVVideoView;

  public PauseCommand(HVVideoView HVVideoView) {
    mHVVideoView = HVVideoView;
  }

  @Override public void execute() {
    mHVVideoView.pause();
    mHVVideoView.stopTimer();
  }
}
