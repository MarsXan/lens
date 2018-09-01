package com.parsadp.lens;


import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;

import java.io.File;

/**
 * Created by Mohsen on 9/1/18.
 */

public class DemoCamService extends HiddenCameraService {

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {

      if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
        CameraConfig cameraConfig = new CameraConfig()
            .getBuilder(this)
            .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
            .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
            .setImageFormat(CameraImageFormat.FORMAT_JPEG)

            .build();

        startCamera(cameraConfig);

        new android.os.Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(DemoCamService.this,
                "Capturing image.", Toast.LENGTH_SHORT).show();

            takePicture();
          }
        }, 2000L);
      } else {

        //Open settings to grant permission for "Draw other apps".
        HiddenCameraUtils.openDrawOverPermissionSetting(this);
      }


    } else {

      //TODO Ask your parent activity for providing runtime permission
      Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
    }
    return START_NOT_STICKY;
  }


  @Override
  public void onImageCapture(@NonNull File imageFile) {
    Toast.makeText(this,
        "Captured image size is : " + imageFile.length(),
        Toast.LENGTH_SHORT)
        .show();
    Log.e("ImagePath",imageFile.getPath());

    // Do something with the image...

    stopSelf();
  }

  public static void addImageToGallery(final String filePath, final Context context) {

    ContentValues values = new ContentValues();

    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
    values.put(MediaStore.MediaColumns.DATA, filePath);

    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
  }

  @Override
  public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
    switch (errorCode) {
      case CameraError.ERROR_CAMERA_OPEN_FAILED:
        //Camera open failed. Probably because another application
        //is using the camera
        Toast.makeText(this, R.string.error_cannot_open, Toast.LENGTH_LONG).show();
        break;
      case CameraError.ERROR_IMAGE_WRITE_FAILED:
        //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
        Toast.makeText(this, R.string.error_cannot_write, Toast.LENGTH_LONG).show();
        break;
      case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
        //camera permission is not available
        //Ask for the camera permission before initializing it.
        Toast.makeText(this, R.string.error_cannot_get_permission, Toast.LENGTH_LONG).show();
        break;
      case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
        //Display information dialog to the user with steps to grant "Draw over other app"
        //permission for the app.
        HiddenCameraUtils.openDrawOverPermissionSetting(this);
        break;
      case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
        Toast.makeText(this, R.string.error_not_having_camera, Toast.LENGTH_LONG).show();
        break;
    }

    stopSelf();
  }
}
