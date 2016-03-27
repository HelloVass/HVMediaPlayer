package org.geeklub.hvmediaplayer.states;

import org.geeklub.hvmediaplayer.widgets.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.HVVideoView;

/**
 * Created by HelloVass on 16/3/26.
 */
public class StartState implements HVMediaController.State {

  @Override public void preparedState(HVVideoView videoView, HVMediaController controller) {

  }

  @Override public void startState(HVVideoView videoView, HVMediaController controller) {

    videoView.start();
    videoView.resetUpdateMediaControllerTimer();

    controller.showOrHidePlayButton(false); //  隐藏“播放”按钮
    controller.showOrHidePauseButton(true); // 显示“暂停”按钮
  }

  @Override public void pauseState(HVVideoView videoView, HVMediaController controller) {
    controller.setState(controller.getPauseState());
    controller.getState().pauseState(videoView, controller);
  }

  @Override public void finishedState(HVVideoView videoView, HVMediaController controller) {
    controller.setState(controller.getFinishedState());
    controller.getState().finishedState(videoView, controller);
  }
}
