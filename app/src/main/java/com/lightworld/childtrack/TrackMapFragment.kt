package com.lightworld.childtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.model.LatLng
import com.baidu.trace.api.entity.OnEntityListener
import com.baidu.trace.api.track.HistoryTrackRequest
import com.baidu.trace.api.track.LatestPointRequest
import com.baidu.trace.api.track.LatestPointResponse
import com.baidu.trace.api.track.OnTrackListener
import com.baidu.trace.model.ProcessOption
import com.baidu.trace.model.SortType
import com.baidu.trace.model.StatusCodes
import com.baidu.trace.model.TraceLocation
import com.lightworld.childtrack.utils.BitmapUtil
import com.lightworld.childtrack.utils.BitmapUtil.bmArrowPoint
import com.lightworld.childtrack.utils.BitmapUtil.bmGeo
import com.lightworld.childtrack.utils.CommonUtil
import com.lightworld.childtrack.utils.MapUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map_track.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * A placeholder fragment containing a simple view.
 */
class TrackMapFragment : Fragment() {
    lateinit var mapUtil: MapUtil
    /**
     * 轨迹点集合
     */
    private var trackPoints: MutableList<LatLng>? = ArrayList()


    /**
     * 历史轨迹请求
     */
    private val historyTrackRequest = HistoryTrackRequest()

    /**
     * 轨迹监听器（用于接收历史轨迹回调）
     */
    private var mTrackListener: OnTrackListener? = null
    val PAGE_SIZE = 5000
    private var pageIndex = 1
    /**
     * 轨迹排序规则
     */
    private var sortType = SortType.asc

    private val mSequenceGenerator = AtomicInteger()
    private lateinit var gatherDispose: Disposable
    private var pointFilter = false
    var tracerMarker: Marker? = null
    var meMarker: Marker? = null
    var shareMyLocalUrl: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_map_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapUtil = MapUtil.getInstance()
        mapUtil.init(tracing_mapView, activity as Context)
        BitmapUtil.init()
        initView()
        initListener()

        gatherDispose = Observable.interval(0, 8, TimeUnit.SECONDS)//每秒发射一个数字出来
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map {
                    otherLocal()
                    myLocal()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { }

//        myLocal()
        //4.处理采集数据 异步 查询历史轨迹 功能需要单独提出来

//        gatherDispose = Observable.interval(0, 8, TimeUnit.SECONDS)//每秒发射一个数字出来
//                .delay(3, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .map {
//                    queryHistoryTrack()
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { }
    }

    private fun otherLocal() {

        val request = LatestPointRequest(mSequenceGenerator.incrementAndGet(), serviceId, lastQueryEntityName)
        val processOption = ProcessOption()
        processOption.isNeedDenoise = true
        processOption.radiusThreshold = 100
        request.processOption = processOption
        LocalManager.mTraceClient.queryLatestPoint(request, object : OnTrackListener() {
            override fun onLatestPointCallback(response: LatestPointResponse?) {

                if (StatusCodes.SUCCESS != response?.getStatus()) {
                    return
                }

                val point = response.getLatestPoint()
                if (null == point || CommonUtil.isZeroPoint(point.location.getLatitude(), point.location
                                .getLongitude())) {
                    return
                }
                var mapUtil = MapUtil.getInstance()

                val currentLatLng = MapUtil.convertTrace2Map(point.location) ?: return

                if (tracerMarker == null) {
                    tracerMarker = mapUtil.addOverlay(currentLatLng, bmGeo, null);
                    mapUtil.animateMapStatus(currentLatLng, 15.0f)
                } else {
                    tracerMarker!!.setPosition(currentLatLng)
                }


            }
        })

    }

    private fun initView() {
        btn_share_mylocal.setOnClickListener {
            var textIntent = Intent(Intent.ACTION_SEND)
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, shareMyLocalUrl)
            startActivity(Intent.createChooser(textIntent, getString(R.string.app_name)))
        }
    }


    private fun initListener() {

        /*   mTrackListener = object : OnTrackListener() {
               override fun onHistoryTrackCallback(response: HistoryTrackResponse?) {
                   val total = response!!.total
                   if (StatusCodes.SUCCESS != response.getStatus()) {
                       activity?.toast(response.getMessage())
                   } else if (0 == total) {
   //                    activity?.toast("未留下任何轨迹信息")
                   } else {
                       val points = response.getTrackPoints()
                       if (null != points) {
                           trackPoints?.clear()//清除之前的
                           for (trackPoint in points) {
                               if (!CommonUtil.isZeroPoint(trackPoint.location.getLatitude(),
                                               trackPoint.location.getLongitude())) {
                                   trackPoints?.add(MapUtil.convertTrace2Map(trackPoint.location))
                               }
                           }
                       }
                   }

                   if (total > PAGE_SIZE * pageIndex) {
                       historyTrackRequest.pageIndex = ++pageIndex
                       queryHistoryTrack()
                   } else {
                       mapUtil.drawHistoryTrack(trackPoints, sortType)
                   }
               }

           }*/
    }

    /**
     * 查询历史轨迹
     */
/*    private fun queryHistoryTrack() {
        historyTrackRequest.setTag(mSequenceGenerator.incrementAndGet())
        historyTrackRequest.setServiceId(serviceId)
        historyTrackRequest.entityName = lastQueryEntityName
        historyTrackRequest.pageIndex = pageIndex
        historyTrackRequest.pageSize = PAGE_SIZE
        */
    /**
     * 查询轨迹的开始时间
     *//*
        var startTime = CommonUtil.getCurrentTime() - 23 * 60 * 60

        */
    /**
     * 查询轨迹的结束时间
     *//*
        var endTime = CommonUtil.getCurrentTime()
        historyTrackRequest.startTime = startTime
        historyTrackRequest.endTime = endTime

        val processOption = ProcessOption()

        if (pointFilter) {
            historyTrackRequest.isProcessed = true//纠偏
            processOption.isNeedDenoise = true//去噪
            processOption.isNeedMapMatch = true//绑路
            processOption.radiusThreshold = 10//精度过滤
        }

        historyTrackRequest.supplementMode = SupplementMode.walking//步行

        historyTrackRequest.processOption = processOption

        LocalManager.mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener)

    }*/

    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    fun myLocal() {
        LocalManager.dealRealLoc(object : OnEntityListener() {
            override fun onReceiveLocation(location: TraceLocation?) {

                if (StatusCodes.SUCCESS != location?.getStatus() || CommonUtil.isZeroPoint(location.getLatitude(),
                                location.getLongitude())) {
                    return
                }
                var mapUtil = MapUtil.getInstance()

                val currentLatLng = MapUtil.convertTraceLocation2Map(location) ?: return

                if (meMarker == null) {
                    meMarker = mapUtil.addOverlay(currentLatLng, bmArrowPoint, null);
                    mapUtil.animateMapStatus(currentLatLng, 15.0f)
                } else {
                    meMarker!!.setPosition(currentLatLng)
                }
                shareMyLocalUrl = "http://api.map.baidu.com/marker?location=${currentLatLng.latitude},${currentLatLng.longitude}&title=followMe&content=iAmHere&output=html&src=UniversalTrack"

//                CurrentLocation.locTime = CommonUtil.toTimeStamp(location.getTime())
//                CurrentLocation.latitude = currentLatLng!!.latitude
//                CurrentLocation.longitude = currentLatLng!!.longitude
//
//                if (null != mapUtil) {
//                    mapUtil.updateStatus(currentLatLng, true)
//                }
            }
        })
    }

    override fun onDestroy() {
        mapUtil.clear()
        trackPoints = null
//        gatherDispose.dispose()
        LocalManager.stopRealLoc()
        super.onDestroy()
    }


}
