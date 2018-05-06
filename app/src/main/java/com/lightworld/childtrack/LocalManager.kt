package com.lightworld.childtrack

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import com.baidu.trace.LBSTraceClient
import com.baidu.trace.Trace
import com.baidu.trace.api.entity.LocRequest
import com.baidu.trace.api.entity.OnEntityListener
import com.baidu.trace.model.OnTraceListener
import com.baidu.trace.model.StatusCodes
import com.lightworld.childtrack.receiver.TrackReceiver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.concurrent.TimeUnit


/**
 * Created by heyue on 2017/12/28.
 */

object LocalManager {
    lateinit var mTrace: Trace
    lateinit var mTraceClient: LBSTraceClient
    private lateinit var realLocDispose: Disposable
    private var powerManager: PowerManager? = null

    private var trackReceiver: TrackReceiver? = null
    private var wakeLock: PowerManager.WakeLock? = null
    fun tipOpenLocal(mContext: Context) {
        val locManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            mContext.alert("即将进入设置页面", "请打开GPS定位,以便更好的使用本软件") {
                yesButton {
                    // 转到手机设置界面，用户设置GPS
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    mContext.startActivity(intent)
                    noButton {
                        mContext.toast("未开启定位,会影响使用哦!")
                    }
                }
            }.show()
        }
    }

    fun initTrace(mContext: Context) {
        // 1.初始化轨迹服务
        mTrace = Trace(serviceId, myTrackEntityName, isNeedObjectStorage)
        // 初始化轨迹服务客户端
        mTraceClient = LBSTraceClient(mContext)
        // 2.设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        powerManager = MyApplication.INSTANCE.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    // 3.开启服务
    fun startTraceService(mTraceListener: OnTraceListener?) {
        mTraceClient.startTrace(mTrace, mTraceListener)
    }

    // 停止服务
    fun stopTraceService(mTraceListener:OnTraceListener?) {
        mTraceClient.stopTrace(mTrace, mTraceListener)
    }

    // 开启采集
    fun startGather(mTraceListener: OnTraceListener?) {
        Handler().postDelayed(Runnable {
            mTraceClient.startGather(mTraceListener)
        }, 4000)
    }

    //停止采集
    fun stopGather(mTraceListener: OnTraceListener?) {
        mTraceClient.stopGather(mTraceListener)
    }



    fun dealRealLoc(entityListener: OnEntityListener) {
        realLocDispose = Observable.interval(0, 10, TimeUnit.SECONDS)//每秒发射一个数字出来
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //实时定位任务
                    val locRequest = LocRequest(serviceId)
                    mTraceClient.queryRealTimeLoc(locRequest, entityListener)
                }
    }

    fun stopRealLoc() {
        realLocDispose?.dispose()
        mTraceClient.stopRealTimeLoc()
    }


    /**
     * 注册广播（电源锁、GPS状态）
     */
     fun registerReceiver() {
        if (isRegisterReceiver) {
            return
        }

        if (null == wakeLock) {
            wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload")
        }
        if (null == trackReceiver) {
            trackReceiver = TrackReceiver(wakeLock)
        }

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(StatusCodes.GPS_STATUS_ACTION)
        MyApplication.INSTANCE?.registerReceiver(trackReceiver, filter)
        isRegisterReceiver = true

    }

     fun unregisterPowerReceiver() {
        if (!isRegisterReceiver) {
            return
        }
        if (null != trackReceiver) {
            MyApplication.INSTANCE?.unregisterReceiver(trackReceiver)
        }
        isRegisterReceiver = false
    }


}

