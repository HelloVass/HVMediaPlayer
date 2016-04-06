package org.geeklub.hvmediaplayer.widgets.factory;

import android.content.Context;

/**
 * Created by HelloVass on 16/4/6.
 */
public class HVVideoPlayerFactory implements AbsHVMediaPlayerFactory {

  private Context mContext;

  public HVVideoPlayerFactory(Context context) {
    mContext = context;
  }

  @Override public HVPlayable createPlayable() {
    return new HVVideoView(mContext);
  }

  @Override public HVController createController() {
    return new HVVideoController(mContext);
  }
}
