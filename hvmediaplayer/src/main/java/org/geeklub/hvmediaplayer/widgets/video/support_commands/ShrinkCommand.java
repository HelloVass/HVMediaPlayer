package org.geeklub.hvmediaplayer.widgets.video.support_commands;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import org.geeklub.hvmediaplayer.utils.DensityUtil;

/**
 * Created by HelloVass on 16/4/13.
 */
public class ShrinkCommand implements Command {

  private Activity mActivity;

  private ViewGroup.LayoutParams mLayoutParams;

  public ShrinkCommand(Activity activity, ViewGroup.LayoutParams layoutParams) {
    mActivity = activity;
    mLayoutParams = layoutParams;
  }

  @Override public void execute() {
    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    mLayoutParams.height = DensityUtil.dip2px(mActivity, 220);
    mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
  }
}
