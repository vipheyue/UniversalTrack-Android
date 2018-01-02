package com.lightworld.childtrack


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lightworld.childtrack.helper.ClipboardManagerHelper
import com.lightworld.childtrack.utils.RxDeviceTool
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

        LocalManager.mTrace.notification = sendNotify()
        LocalManager.tipOpenLocal(activity as Context)
        LocalManager.startTraceService()
        LocalManager.startGather()
        //TODO 目前假设没有任何其他 情况发生 需要单独处理
//        LocalManager.dealRealLoc()
        ClipboardManagerHelper.discernSymbol(activity!!)


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

    @TargetApi(Build.VERSION_CODES.O)
    fun sendNotify(): Notification? {
        var notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val NOTIFICATION_ID = 1
        val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        val pattern = longArrayOf(0, 100, 1000, 300, 200, 100, 500, 200, 100)
        val appName = resources.getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, appName, NotificationManager.IMPORTANCE_DEFAULT)
            // Configure the notification channel.
            notificationChannel.description = appName + " APP 的描述"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
//            notificationChannel.vibrationPattern = pattern
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

        }


        val mBuilder = NotificationCompat.Builder(activity as Context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(appName)
                .setContentText("正在为您服务")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission

        val resultIntent = Intent(activity as Context, MainActivity::class.java)
        val resultPendingIntent =
                PendingIntent.getActivity(activity as Context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        mBuilder.setContentIntent(resultPendingIntent)

//        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
        return mBuilder.build()
    }
}// Required empty public constructor
