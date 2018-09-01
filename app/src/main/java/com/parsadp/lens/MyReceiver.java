package com.parsadp.lens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Mohsen on 9/1/18.
 */

public class MyReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Toast.makeText(context,"Message",Toast.LENGTH_LONG).show();
    context.startService(new Intent(context,CamService.class));
  }
}