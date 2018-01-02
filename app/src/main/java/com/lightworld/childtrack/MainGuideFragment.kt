package com.lightworld.childtrack


import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main_guide.*
import org.jetbrains.anko.startActivity


/**
 * A simple [Fragment] subclass.
 */
class MainGuideFragment : Fragment() {

    private var powerManager: PowerManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myTrackEntityName = RxDeviceTool.getDeviceIdIMEI(activity).toString()
        powerManager = activity!!.getSystemService(Context.POWER_SERVICE) as PowerManager

        rtv_my_track.setOnClickListener { activity!!.startActivity<TrackMapActivity>(TRACK_ENTITY_NAME to myTrackEntityName) }
        rtv_share_track.setOnClickListener { activity!!.startActivity<TrackMeActivity>() }
        rtv_track_other.setOnClickListener { activity!!.startActivity<TrackOtherActivity>() }
        LocalManager.tipOpenLocal(activity as Context)
        LocalManager.startTraceService()
        LocalManager.startGather()
        //TODO 目前假设没有任何其他 情况发生 需要单独处理
//        LocalManager.dealRealLoc()

        //获取 剪切板
        val cm = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = cm.primaryClip.getItemAt(0).text
        if (copyText.contains('¥') && copyText.endsWith('¥')) {
            var split: List<String> = copyText.split('¥')
            var id = split.get(1)
            activity!!.startActivity<TrackMapActivity>(TRACK_ENTITY_NAME to id)
            //重置剪切板
            val mClipData = ClipData.newPlainText("", "")
            cm.primaryClip = mClipData
        }
    }

    override fun onDestroy() {
        LocalManager.stopTraceService()
        super.onDestroy()
    }



    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = activity!!.packageName
            val isIgnoring = powerManager?.isIgnoringBatteryOptimizations(packageName)
            if (!isIgnoring!!) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:" + packageName)
                try {
                    startActivity(intent)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }
    }

}// Required empty public constructor
