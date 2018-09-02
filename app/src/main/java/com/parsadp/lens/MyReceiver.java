package com.parsadp.lens;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/** Created by Mohsen on 9/1/18. */
public class MyReceiver extends BroadcastReceiver {

  public static boolean kesafat = false;

  public static int counter = 0;

  // bad practice. it should move to application file
  public static KeyguardManager.KeyguardLock sKeyguardLock;

  // bad practice. it should move to application file
  public static void reEnableKeyGuardLock() {
    if (sKeyguardLock != null) sKeyguardLock.reenableKeyguard();
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Toast.makeText(context, "broad cast received", Toast.LENGTH_SHORT).show();
    int currentVol = intent.getExtras().getInt("android.media.EXTRA_VOLUME_STREAM_VALUE");
    int prevVol = intent.getExtras().getInt("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE");
    if (currentVol < prevVol) {
      counter++;
      Toast.makeText(context, "start main act\n number " + counter, Toast.LENGTH_SHORT).show();
      kesafat = true;

      context.startActivity(
          new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

      //      context.startService(new Intent(context, CamService.class));
    }
  }
}
