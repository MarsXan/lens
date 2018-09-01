package com.parsadp.lens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Created by Mohsen on 9/1/18. */
public class MyReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    int currentVol = intent.getExtras().getInt("android.media.EXTRA_VOLUME_STREAM_VALUE");
    int prevVol = intent.getExtras().getInt("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE");
    if (currentVol < prevVol) context.startService(new Intent(context, CamService.class));
  }
}
