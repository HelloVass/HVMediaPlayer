package org.geeklub.hvmediaplayer.states;

import org.geeklub.hvmediaplayer.widgets.HVMediaController;
import org.geeklub.hvmediaplayer.widgets.HVVideoView;

/**
 * Created by HelloVass on 16/3/26.
 */
public class PreparedState implements HVMediaController.State {

  @Override public void preparedState(HVVideoView videoView, HVMediaController controller) {

    controller.showOrHidePauseButton(false); // 隐藏“暂停”按钮
    controller.showOrHidePlayButton(true); // 显示“播放”按钮

    controller.setCurrentTime(0); // 当前显示的时间
    controller.setEndTime(videoView.getDuration()); // 总时间
  }

  @Override public void startState(HVVideoView videoView, HVMediaController controller) {
    controller.setState(controller.getStartState());
    controller.getState().startState(videoView, controller);
  }

  @Override public void pauseState(HVVideoView videoView, HVMediaController controller) {

  }

  @Override public void finishedState(HVVideoView videoView, HVMediaController controller) {

  }
}
