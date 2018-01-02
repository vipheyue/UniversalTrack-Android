package com.lightworld.childtrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lightworld.childtrack.helper.ClipboardManagerHelper
import io.github.xudaojie.qrcodelib.CaptureActivity
import kotlinx.android.synthetic.main.activity_track_other.*
import org.jetbrains.anko.startActivity


class TrackOtherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_other)
        initView()
    }


    private val REQUEST_QR_CODE: Int = 111

    private fun initView() {
        rtv_scan.setOnClickListener {
            val i = Intent(this, CaptureActivity::class.java)
            startActivityForResult(i, REQUEST_QR_CODE)
        }
        rtv_startTrack.setOnClickListener {
            val entityName = autoCompleteTextView.text.toString().trim()
            val userId = ClipboardManagerHelper.splitSymbol(entityName)
            if (userId.isNotEmpty()) {
                startActivity<TrackMapActivity>(TRACK_ENTITY_NAME to userId)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_QR_CODE
                && data != null) {
            val result = data.getStringExtra("result")
//            Toast.makeText(this@TrackOtherActivity, result, Toast.LENGTH_SHORT).show()
            startActivity<TrackMapActivity>(TRACK_ENTITY_NAME to result)
        }
    }

}
