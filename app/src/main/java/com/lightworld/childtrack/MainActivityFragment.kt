package com.lightworld.childtrack

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.baidu.trace.LBSTraceClient
import com.baidu.trace.Trace
import com.baidu.trace.api.track.LatestPointRequest
import com.baidu.trace.api.track.LatestPointResponse
import com.baidu.trace.api.track.OnTrackListener
import com.baidu.trace.model.OnTraceListener
import com.baidu.trace.model.ProcessOption
import com.baidu.trace.model.PushMessage
import org.jetbrains.anko.doAsync
import java.util.concurrent.atomic.AtomicInteger


/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {
    private lateinit var mTrace: Trace
    private lateinit var mTraceClient: LBSTraceClient
    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
//    private var trackListener: OnTrackListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1.初始化轨迹服务
        mTrace = Trace(serviceId, entityName, isNeedObjectStorage)
        // 初始化轨迹服务客户端
        mTraceClient = LBSTraceClient(activity)
        // 2.设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        // 3.开启服务 开启采集
        mTraceClient.startTrace(mTrace, mTraceListener)

        Handler().postDelayed(Runnable {
            mTraceClient.startGather(mTraceListener)
        }, 3000)

        //4.处理采集数据 异步 查询历史轨迹 功能需要单独提出来
        Handler().postDelayed(Runnable {
            activity.doAsync {
                val request = LatestPointRequest(AtomicInteger().incrementAndGet(), serviceId, entityName)
                val processOption = ProcessOption()
                processOption.isNeedDenoise = true
                processOption.radiusThreshold = 100
                request.processOption = processOption
                mTraceClient.queryLatestPoint(request, trackListener)
            }
        }, 4000)


    }

    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    object trackListener : OnTrackListener() {
        override fun onLatestPointCallback(p0: LatestPointResponse?) {
            super.onLatestPointCallback(p0)

        }

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

    override fun onDestroy() {
        // 停止服务
        mTraceClient.stopTrace(mTrace, mTraceListener);
        super.onDestroy()
    }
}
