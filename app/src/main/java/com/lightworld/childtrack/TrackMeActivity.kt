package com.lightworld.childtrack

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.xudaojie.qrcodelib.common.RxQRCode
import kotlinx.android.synthetic.main.activity_track_me.*

class TrackMeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_me)
        initView()
    }

    private fun initView() {
        //二维码生成方式一  推荐此方法
        RxQRCode.builder("str").
                backColor(getResources().getColor(R.color.white)).
                codeColor(getResources().getColor(R.color.colorPrimaryDark)).
                codeSide(600).
                into(imageView);
    }
}
