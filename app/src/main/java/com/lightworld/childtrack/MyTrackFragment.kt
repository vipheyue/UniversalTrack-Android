package com.lightworld.childtrack

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_track.*


/**
 * A placeholder fragment containing a simple view.
 */
class MyTrackFragment : Fragment() {
    lateinit var mapUtil: MapUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_my_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapUtil = MapUtil.getInstance()
        mapUtil.init(tracing_mapView,activity as Context)
        BitmapUtil.init()

        LocalManager.dealGatherData(activity as Context)

    }

    override fun onDestroy() {
        mapUtil.clear()

        super.onDestroy()
    }
}
