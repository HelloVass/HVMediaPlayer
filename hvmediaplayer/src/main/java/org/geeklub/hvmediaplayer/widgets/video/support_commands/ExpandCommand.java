package org.geeklub.hvmediaplayer.widgets.video.support_commands;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import org.geeklub.hvmediaplayer.utils.DensityUtil;

/**
 * Created by HelloVass on 16/4/13.
 */
public class ExpandCommand implements Command {

  private Activity mActivity;

  private ViewGroup.LayoutParams mLayoutParams;

  public ExpandCommand(Activity activity, ViewGroup.LayoutParams layoutParams) {
    mActivity = activity;
    mLayoutParams = layoutParams;
  }

  @Override public void execute() {

    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    mLayoutParams.width = DensityUtil.dip2px(mActivity, 1920);
    mLayoutParams.height = DensityUtil.dip2px(mActivity, 1080);
  }
}
