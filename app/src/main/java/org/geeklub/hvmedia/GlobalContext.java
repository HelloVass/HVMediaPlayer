package org.geeklub.hvmedia;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by HelloVass on 16/4/22.
 */
public class GlobalContext extends Application {

  @Override public void onCreate() {
    super.onCreate();
    LeakCanary.install(this);
  }
}
