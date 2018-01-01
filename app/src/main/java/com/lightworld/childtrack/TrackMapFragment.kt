package com.lightworld.childtrack

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baidu.mapapi.model.LatLng
import com.baidu.trace.api.track.*
import com.baidu.trace.model.ProcessOption
import com.baidu.trace.model.SortType
import com.baidu.trace.model.StatusCodes
import kotlinx.android.synthetic.main.fragment_map_track.*
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


/**
 * A placeholder fragment containing a simple view.
 */
class TrackMapFragment : Fragment() {
    lateinit var mapUtil: MapUtil

    /**
     * 查询轨迹的开始时间
     */
    private var startTime = CommonUtil.getCurrentTime() - 1000

    /**
     * 查询轨迹的结束时间
     */
    private var endTime = CommonUtil.getCurrentTime()
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_map_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapUtil = MapUtil.getInstance()
        mapUtil.init(tracing_mapView, activity as Context)
        BitmapUtil.init()
//        LocalManager.queryLastPoint(activity as Context)
        initListener()
        queryHistoryTrack()
    }

    private fun initListener() {

        mTrackListener = object : OnTrackListener() {
            override fun onHistoryTrackCallback(response: HistoryTrackResponse?) {
                val total = response!!.total
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    activity?.toast(response.getMessage())

                } else if (0 == total) {
                    activity?.toast("no_track_data")

                } else {
                    val points = response.getTrackPoints()
                    if (null != points) {
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

            override fun onDistanceCallback(response: DistanceResponse?) {
                super.onDistanceCallback(response)
            }

            override fun onLatestPointCallback(response: LatestPointResponse?) {
                super.onLatestPointCallback(response)
            }
        }
    }

    /**
     * 查询历史轨迹
     */
    private fun queryHistoryTrack() {
        historyTrackRequest.setTag(mSequenceGenerator.incrementAndGet())
        historyTrackRequest.setServiceId(serviceId)
        historyTrackRequest.entityName = lastQueryEntityName
        historyTrackRequest.pageIndex = pageIndex
        historyTrackRequest.pageSize = PAGE_SIZE

        historyTrackRequest.startTime = startTime
        historyTrackRequest.endTime = endTime

        val processOption = ProcessOption()
        historyTrackRequest.setProcessed(true)//纠偏
        processOption.isNeedMapMatch = true//绑路
        historyTrackRequest.supplementMode = SupplementMode.walking//步行

        historyTrackRequest.processOption = processOption

        LocalManager.mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener)
    }

    override fun onDestroy() {
        mapUtil.clear()
        trackPoints = null

        super.onDestroy()
    }
}
