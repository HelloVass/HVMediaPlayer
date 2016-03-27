package org.geeklub.hvmediaplayer.states;

import org.geeklub.hvmediaplayer.widgets.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.HVVideoView;

/**
 * Created by HelloVass on 16/3/26.
 */
public class PauseState implements HVMediaController.State {

  @Override public void preparedState(HVVideoView videoView, HVMediaController controller) {

  }

  @Override public void startState(HVVideoView videoView, HVMediaController controller) {
    controller.setState(controller.getStartState());
    controller.getState().startState(videoView, controller);
  }

  @Override public void pauseState(HVVideoView videoView, HVMediaController controller) {

    videoView.pause();

    controller.showOrHidePauseButton(false); // 隐藏"暂停"按钮
    controller.showOrHidePlayButton(true);  // 显示播放按钮
  }

  @Override public void finishedState(HVVideoView videoView, HVMediaController controller) {
    controller.setState(controller.getFinishedState());
    controller.getState().finishedState(videoView, controller);
  }
}
