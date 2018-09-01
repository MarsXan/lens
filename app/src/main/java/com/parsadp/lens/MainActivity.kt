package com.parsadp.lens

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


//    val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//    val kl = km.newKeyguardLock("MyKeyguardLock")
//    kl.disableKeyguard()
//    findViewById<Button>(R.id.btn).setOnClickListener{kl.reenableKeyguard()
//}
  }
}
