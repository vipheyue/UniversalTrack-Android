package com.lightworld.childtrack

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.xudaojie.qrcodelib.common.RxQRCode
import kotlinx.android.synthetic.main.activity_track_me.*
import org.jetbrains.anko.toast

class TrackMeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_me)
        initView()
    }

    private fun initView() {
        val mActionBar = supportActionBar
        mActionBar!!.setHomeButtonEnabled(true)
        mActionBar.setDisplayHomeAsUpEnabled(true)
        mActionBar.title = "追踪我"

        tv_id.setText("我的身份ID:" + myTrackEntityName)
        //二维码生成方式一  推荐此方法
        RxQRCode.builder(myTrackEntityName).
                backColor(getResources().getColor(R.color.white)).
                codeColor(getResources().getColor(R.color.colorPrimaryDark)).
                codeSide(600).
                into(imageView);
        rtv_copy.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val tempString = "长按复制,打开全能追踪APP,查看我的轨迹\n ¥" + myTrackEntityName + "¥"
            val mClipData = ClipData.newPlainText("身份ID", tempString)
            cm.primaryClip = mClipData

            toast("已复制剪切板---> " + myTrackEntityName)
            var textIntent = Intent(Intent.ACTION_SEND);
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, tempString)
            startActivity(Intent.createChooser(textIntent, "邀请查看我的轨迹"))
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
