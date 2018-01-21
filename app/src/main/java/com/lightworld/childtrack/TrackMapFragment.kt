package com.lightworld.childtrack

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baidu.mapapi.model.LatLng
import com.baidu.trace.api.track.HistoryTrackRequest
import com.baidu.trace.api.track.HistoryTrackResponse
import com.baidu.trace.api.track.OnTrackListener
import com.baidu.trace.api.track.SupplementMode
import com.baidu.trace.model.ProcessOption
import com.baidu.trace.model.SortType
import com.baidu.trace.model.StatusCodes
import com.lightworld.childtrack.utils.BitmapUtil
import com.lightworld.childtrack.utils.CommonUtil
import com.lightworld.childtrack.utils.MapUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map_track.*
import org.jetbrains.anko.toast
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
//        queryHistoryTrack()
        queryLastPoint(activity!!)
        initView()
    }

    private fun initView() {

        switch_filter.setOnCheckedChangeListener { buttonView, isChecked ->
            pointFilter = isChecked
        }
    }

    fun queryLastPoint(mContext: Context) {
        //4.处理采集数据 异步 查询历史轨迹 功能需要单独提出来

        gatherDispose = Observable.interval(0, 8, TimeUnit.SECONDS)//每秒发射一个数字出来
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map {
                    queryHistoryTrack()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { }
    }

    private fun initListener() {

        mTrackListener = object : OnTrackListener() {
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
        /**
         * 查询轨迹的开始时间
         */
        var startTime = CommonUtil.getCurrentTime() - 23 * 60 * 60

        /**
         * 查询轨迹的结束时间
         */
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
    }

    override fun onDestroy() {
        mapUtil.clear()
        trackPoints = null
        gatherDispose.dispose()
        super.onDestroy()
    }
}
