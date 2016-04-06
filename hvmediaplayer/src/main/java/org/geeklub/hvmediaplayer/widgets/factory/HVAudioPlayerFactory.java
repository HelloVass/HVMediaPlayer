package org.geeklub.hvmediaplayer.widgets.factory;

import android.content.Context;

/**
 * Created by HelloVass on 16/4/6.
 */
public class HVAudioPlayerFactory implements AbsHVMediaPlayerFactory {

  private Context mContext;

  public HVAudioPlayerFactory(Context context) {
    mContext = context;
  }

  @Override public HVPlayable createPlayable() {
    return new HVAudioView(mContext);
  }

  @Override public HVController createController() {
    return new HVAudioController(mContext);
  }
}
