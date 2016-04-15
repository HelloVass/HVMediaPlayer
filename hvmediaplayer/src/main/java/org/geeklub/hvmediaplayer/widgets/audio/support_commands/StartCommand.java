package org.geeklub.hvmediaplayer.widgets.audio.support_commands;

import org.geeklub.hvmediaplayer.widgets.audio.HVAudioView;

/**
 * Created by HelloVass on 16/4/13.
 */
public class StartCommand implements Command {

  private HVAudioView mHVAudioView;

  public StartCommand(HVAudioView HVAudioView) {
    mHVAudioView = HVAudioView;
  }

  @Override public void execute() {
    mHVAudioView.start();
    mHVAudioView.resetTimer();
  }
}
