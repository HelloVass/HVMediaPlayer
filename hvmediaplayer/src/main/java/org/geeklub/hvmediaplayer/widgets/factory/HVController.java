package org.geeklub.hvmediaplayer.widgets.factory;

/**
 * Created by HelloVass on 16/4/6.
 */
public interface HVController {

  void hide();

  void show();

  void hidePlayButton();

  void showPlayButton();

  void hidePauseButton();

  void showPauseButton();

  void setCurrentTime(long timeInMillis);

  void setEndTime(long timeInMillis);

  void setSeekBarProgress(int progress);

  void setSeekBarSecondaryProgress(int secondaryProgress);

  boolean isDraggingSeekBar();
}
