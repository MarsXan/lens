package com.parsadp.lens;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.util.Random;

import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by Mohsen on 9/1/18.
 */

public class CamService extends HiddenCameraService {

  private KeyguardManager.KeyguardLock mKeyguardLock;

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
            Toast.makeText(CamService.this,
                "Capturing image.", Toast.LENGTH_SHORT).show();

            takePicture();
          }
        }, 100L);
      } else {

        //Open settings to grant permission for "Draw other apps".
        HiddenCameraUtils.openDrawOverPermissionSetting(this);
      }
    } else {

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
    Log.e("ImagePathtt", imageFile.getAbsolutePath());

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.RGB_565;
    Bitmap bitmap =
        RotateBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options), 90);
    SaveImage(bitmap);

    unlockScreen();

    startOfficeLensActivity();

    stopSelf();
  }

  private void unlockScreen() {
    KeyguardManager keyguardManager =
            (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    mKeyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
    mKeyguardLock.disableKeyguard();
  }

  private Bitmap RotateBitmap(Bitmap source, float angle) {
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
  }

    private void startOfficeLensActivity() {
        try{
            Intent i = getPackageManager().getLaunchIntentForPackage("com.microsoft.office.officelens");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Office Lens Application is not installed !", Toast.LENGTH_LONG).show();
        }
    }

  private void SaveImage(Bitmap finalBitmap) {

    String root = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES).toString();
    File myDir = new File(root + "/saved_images");
    myDir.mkdirs();
    Random generator = new Random();

    int n = 10000;
    n = generator.nextInt(n);
    String fname = "Image-" + n + ".jpg";
    File file = new File(myDir, fname);
    if (file.exists()) file.delete();
    try {
      FileOutputStream out = new FileOutputStream(file);
      finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Tell the media scanner about the new file so that it is
    // immediately available to the user.
    MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
        new MediaScannerConnection.OnScanCompletedListener() {
          public void onScanCompleted(String path, Uri uri) {
            Log.i("ExternalStorage", "Scanned " + path + ":");
            Log.i("ExternalStorage", "-> uri=" + uri);
          }
        });
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
        Toast.makeText(this, R.string.error_cannot_get_permission, Toast.LENGTH_LONG).show();
        HiddenCameraUtils.openDrawOverPermissionSetting(this);
        break;
      case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
        Toast.makeText(this, R.string.error_not_having_camera, Toast.LENGTH_LONG).show();
        break;
    }

    stopSelf();
  }
}
