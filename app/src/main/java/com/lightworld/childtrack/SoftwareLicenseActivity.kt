package com.lightworld.childtrack

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import com.lightworld.childtrack.utils.ReadAssetsJsonUtils
import kotlinx.android.synthetic.main.activity_software_license.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*


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
        button.setOnClickListener {
            if (checkBox.isChecked) {
                configAgreeLicense = true
                checkMiss()
            } else {
                toast("请勾选同意软件协议")
            }
        }
    }

    private fun checkMiss() {
        // 适配android M，检查权限
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isNeedRequestPermissions(permissions)) {
                requestPermissions(permissions.toTypedArray(), 0)
            } else {//进入软件
                startActivity<MainActivity>()
                finish()
            }
        } else {//进入软件
            startActivity<MainActivity>()
            finish()
        }
    }

    private fun isNeedRequestPermissions(permissions: MutableList<String>): Boolean {
        // 定位精确位置
        addPermission(permissions, Manifest.permission.ACCESS_FINE_LOCATION)
        // 存储权限
        addPermission(permissions, Manifest.permission.READ_PHONE_STATE)
        // 读取手机状态
        addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        addPermission(permissions, Manifest.permission.CAMERA)
        return permissions.size > 0
    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
        }
    }
}
