package org.geeklub.hvmediaplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by HelloVass on 16/3/22.
 */
public class TimeUtil {

  public static final SimpleDateFormat DEFAULT_DATE_FORMAT =
      new SimpleDateFormat("mm:ss", Locale.getDefault());

  private TimeUtil() {

  }

  public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
    return dateFormat.format(new Date(timeInMillis));
  }

  public static String getTime(long timeInMillis) {
    return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
  }
}
