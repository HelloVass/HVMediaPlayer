package org.geeklub.hvmediaplayer.widgets.video.support_commands;

import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * Created by HelloVass on 16/4/13.
 */
public class ShrinkCommand implements Command {

  private Activity mActivity;

  public ShrinkCommand(Activity activity) {
    mActivity = activity;
  }

  @Override public void execute() {
    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }
}
