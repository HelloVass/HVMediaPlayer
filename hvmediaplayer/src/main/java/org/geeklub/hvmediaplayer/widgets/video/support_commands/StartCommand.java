package org.geeklub.hvmediaplayer.widgets.video.support_commands;

import org.geeklub.hvmediaplayer.widgets.video.HVVideoView;

/**
 * Created by HelloVass on 16/4/13.
 */
public class StartCommand implements Command {

  private HVVideoView mHVVideoView;

  public StartCommand(HVVideoView HVVideoView) {
    mHVVideoView = HVVideoView;
  }

  @Override public void execute() {
    mHVVideoView.start();
    mHVVideoView.resetTimer();
  }
}
