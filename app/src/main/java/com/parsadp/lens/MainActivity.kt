package com.parsadp.lens

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mKeyguardLock: KeyguardManager.KeyguardLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (MyReceiver.kesafat) {
            unlockScreen()
            EasyImage.openCamera(this, EasyImage.REQ_TAKE_PICTURE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : EasyImage.Callbacks {
            override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource, type: Int) {

            }

            override fun onImagePicked(imageFile: File, source: EasyImage.ImageSource, type: Int) {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                SaveImage(BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options))


               finish()

            }

            override fun onCanceled(source: EasyImage.ImageSource, type: Int) {

            }
        })

    }

    private fun startOfficeLensActivity() {
        try {
            val i = packageManager.getLaunchIntentForPackage("com.microsoft.office.officelens")
            startActivity(i)
        } catch (e: Exception) {
            Toast.makeText(this, "Office Lens Application is not installed !", Toast.LENGTH_LONG).show()
        }
    }

    private fun unlockScreen() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        mKeyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock")
        mKeyguardLock?.disableKeyguard()
    }

    private fun SaveImage(finalBitmap: Bitmap) {

        val root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString()
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val generator = Random()

        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        startOfficeLensActivity()
    }
}
