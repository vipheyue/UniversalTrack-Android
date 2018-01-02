package com.lightworld.childtrack

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.provider.Settings
import com.baidu.trace.LBSTraceClient
import com.baidu.trace.Trace
import com.baidu.trace.api.entity.LocRequest
import com.baidu.trace.api.entity.OnEntityListener
import com.baidu.trace.api.track.LatestPointRequest
import com.baidu.trace.api.track.LatestPointResponse
import com.baidu.trace.api.track.OnTrackListener
import com.baidu.trace.model.*
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
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by heyue on 2017/12/28.
 */

object LocalManager {
    private lateinit var mTrace: Trace
    lateinit var mTraceClient: LBSTraceClient
    private lateinit var realLocDispose: Disposable
    private lateinit var gatherDispose: Disposable
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
        powerManager= MyApplication.INSTANCE.getSystemService(Context.POWER_SERVICE) as PowerManager

    }

    // 3.开启服务
    fun startTraceService() {
        mTraceClient.startTrace(mTrace, mTraceListener)
    }

    // 停止服务
    fun stopTraceService() {
        mTraceClient.stopTrace(mTrace, mTraceListener)
    }

    // 开启采集
    fun startGather() {
        Handler().postDelayed(Runnable {
            mTraceClient.startGather(mTraceListener)
        }, 4000)
    }

    //停止采集
    fun stopGather() {
        mTraceClient.stopGather(mTraceListener)
        gatherDispose?.dispose()
    }


    fun queryLastPoint(mContext: Context) {
        //4.处理采集数据 异步 查询历史轨迹 功能需要单独提出来

        gatherDispose = Observable.interval(0, 5, TimeUnit.SECONDS)//每秒发射一个数字出来
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map {
                    val request = LatestPointRequest(AtomicInteger().incrementAndGet(), serviceId, myTrackEntityName)
                    val processOption = ProcessOption()
                    processOption.isNeedDenoise = true
                    processOption.radiusThreshold = 100
                    request.processOption = processOption
                    mTraceClient.queryLatestPoint(request, trackListener)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { }
    }

    fun dealRealLoc() {
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

    object mTraceListener : OnTraceListener {
        override fun onBindServiceCallback(p0: Int, p1: String?) {
        }

        override fun onInitBOSCallback(p0: Int, p1: String?) {
        }

        // 开启服务回调
        override fun onStartTraceCallback(errorNo: Int, message: String) {
            if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                registerReceiver()
            }
        }

        // 停止服务回调
        override fun onStopTraceCallback(errorNo: Int, message: String) {
            if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                unregisterPowerReceiver()
            }
        }

        // 开启采集回调
        override fun onStartGatherCallback(status: Int, message: String) {}

        // 停止采集回调
        override fun onStopGatherCallback(status: Int, message: String) {

        }

        // 推送回调
        override fun onPushCallback(messageNo: Byte, message: PushMessage) {}
    }


    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    object trackListener : OnTrackListener() {
        override fun onLatestPointCallback(response: LatestPointResponse?) {
            if (StatusCodes.SUCCESS != response?.getStatus()) {
                return
            }

            val point = response.getLatestPoint()
            if (null == point || CommonUtil.isZeroPoint(point.location.getLatitude(), point.location.getLongitude())) {
                return
            }

            val currentLatLng = MapUtil.convertTrace2Map(point.location) ?: return
            MapUtil.getInstance().updateStatus(currentLatLng, true)
        }
    }

    object entityListener : OnEntityListener() {
        override fun onReceiveLocation(p0: TraceLocation?) {
            super.onReceiveLocation(p0)
        }
    }

    object realTimeHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }
    /**
     * 注册广播（电源锁、GPS状态）
     */
    private fun registerReceiver() {
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

    private fun unregisterPowerReceiver() {
        if (!isRegisterReceiver) {
            return
        }
        if (null != trackReceiver) {
            MyApplication.INSTANCE?.unregisterReceiver(trackReceiver)
        }
        isRegisterReceiver = false
    }
}

