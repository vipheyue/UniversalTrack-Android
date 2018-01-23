package com.lightworld.childtrack

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import com.lightworld.childtrack.utils.ReadAssetsJsonUtils
import kotlinx.android.synthetic.main.activity_software_license.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast



class SoftwareLicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_software_license)
        initView()
    }

    private fun initView() {
        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
        val open = getAssets().open("PrivacyPolicy.txt")
        val readJson = ReadAssetsJsonUtils.read(open)
        editText.setText(readJson)
        checkBox.setOnClickListener {
            //权限处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//偷懒了 哈哈 没做拒绝处理
                var string_array: Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                requestPermissions(string_array, 0)
            }
        }

        button.setOnClickListener {
            if (checkBox.isChecked) {
                configAgreeLicense = true
                startActivity<MainActivity>()
                finish()
            } else {
                toast("请勾选同意软件协议")
            }
        }
    }
}
