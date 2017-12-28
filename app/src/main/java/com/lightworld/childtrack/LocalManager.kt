package com.lightworld.childtrack

import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.baidu.trace.LBSTraceClient
import com.baidu.trace.Trace
import com.baidu.trace.api.entity.LocRequest
import com.baidu.trace.api.entity.OnEntityListener
import com.baidu.trace.api.track.LatestPointRequest
import com.baidu.trace.api.track.LatestPointResponse
import com.baidu.trace.api.track.OnTrackListener
import com.baidu.trace.model.OnTraceListener
import com.baidu.trace.model.ProcessOption
import com.baidu.trace.model.PushMessage
import com.baidu.trace.model.TraceLocation
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by heyue on 2017/12/28.
 */

object LocalManager {
    private lateinit var mTrace: Trace
    private lateinit var mTraceClient: LBSTraceClient

    fun initTrace(mContext: Context) {
        // 1.初始化轨迹服务
        mTrace = Trace(serviceId, entityName, isNeedObjectStorage)
        // 初始化轨迹服务客户端
        mTraceClient = LBSTraceClient(mContext)
        // 2.设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);
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
        }, 2000)
    }

    //停止采集
    fun stopGather() {
        mTraceClient.stopGather(mTraceListener)
    }


    fun dealGatherData(mContext: Context) {
        //4.处理采集数据 异步 查询历史轨迹 功能需要单独提出来
        Handler().postDelayed(Runnable {
            mContext.doAsync {
                val request = LatestPointRequest(AtomicInteger().incrementAndGet(), serviceId, entityName)
                val processOption = ProcessOption()
                processOption.isNeedDenoise = true
                processOption.radiusThreshold = 100
                request.processOption = processOption
                mTraceClient.queryLatestPoint(request, trackListener)
            }
        }, 3000)
    }

    fun dealRealLoc() {

        val observable = Observable.interval(0, 10, TimeUnit.SECONDS)//每秒发射一个数字出来
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logger.d(it.toString())

                }

//        realTimeHandler.post(realTimeLocRunnable)
        /*   Handler().postDelayed(Runnable {
            //            mContext.doAsync {
            val locRequest = LocRequest(serviceId)
            mTraceClient.queryRealTimeLoc(locRequest, entityListener)
//            }
        }, 4000)*/
    }

    fun stopRealLoc() {
        mTraceClient.stopRealTimeLoc()
    }

    object mTraceListener : OnTraceListener {
        override fun onBindServiceCallback(p0: Int, p1: String?) {
        }

        override fun onInitBOSCallback(p0: Int, p1: String?) {
        }

        // 开启服务回调
        override fun onStartTraceCallback(status: Int, message: String) {
            Toast.makeText(MyApplication.INSTANCE, "开启服务回调", Toast.LENGTH_SHORT).show()
        }

        // 停止服务回调
        override fun onStopTraceCallback(status: Int, message: String) {}

        // 开启采集回调
        override fun onStartGatherCallback(status: Int, message: String) {}

        // 停止采集回调
        override fun onStopGatherCallback(status: Int, message: String) {}

        // 推送回调
        override fun onPushCallback(messageNo: Byte, message: PushMessage) {}
    }


    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    object trackListener : OnTrackListener() {
        override fun onLatestPointCallback(p0: LatestPointResponse?) {
            super.onLatestPointCallback(p0)
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
     * 实时定位任务
     *
     * @author baidu
     */
    object realTimeLocRunnable : Runnable {

        private val interval = 10

        override fun run() {
            val locRequest = LocRequest(serviceId)
            mTraceClient.queryRealTimeLoc(locRequest, entityListener)
            realTimeHandler.postDelayed(this, (interval * 1000).toLong())
        }
    }
}

