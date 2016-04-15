package org.geeklub.hvmediaplayer.widgets.audio.support_commands;

import org.geeklub.hvmediaplayer.widgets.audio.HVAudioView;

/**
 * Created by HelloVass on 16/4/13.
 */
public class PauseCommand implements Command {

  private HVAudioView mHVAudioView;

  public PauseCommand(HVAudioView HVAudioView) {
    mHVAudioView = HVAudioView;
  }

  @Override public void execute() {
    mHVAudioView.pause();
    mHVAudioView.stopTimer();
  }
}
