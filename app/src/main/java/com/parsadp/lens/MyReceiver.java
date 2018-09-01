package com.parsadp.lens;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Created by Mohsen on 9/1/18. */
public class MyReceiver extends BroadcastReceiver {

  public static boolean kesafat = false;

  // bad practice. it should move to application file
  public static KeyguardManager.KeyguardLock sKeyguardLock;

  // bad practice. it should move to application file
  public static void reEnableKeyGuardLock() {
    if (sKeyguardLock != null) sKeyguardLock.reenableKeyguard();
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    int currentVol = intent.getExtras().getInt("android.media.EXTRA_VOLUME_STREAM_VALUE");
    int prevVol = intent.getExtras().getInt("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE");
    if (currentVol == prevVol) {

      kesafat = true;

      context.startActivity(
          new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

      //      context.startService(new Intent(context, CamService.class));
    }
  }
}
