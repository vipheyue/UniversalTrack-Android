package com.lightworld.childtrack

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        1、设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        2、 设置无title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash)

        textView.setText(resources.getString(R.string.app_name) + "\n now" )
        Handler().postDelayed(Runnable {
            goAct()
        }, 2000)

    }

    private fun goAct() {
        if (configAgreeLicense) {
            startActivity<MainActivity>()//  startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity<SoftwareLicenseActivity>()
        }
        finish()
    }
}
