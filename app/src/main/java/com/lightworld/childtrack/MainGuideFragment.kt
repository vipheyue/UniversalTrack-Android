package com.lightworld.childtrack


import android.content.Context
import android.os.Bundle
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rtv_my_track.setOnClickListener { activity!!.startActivity<MyTrackActivity>() }
        LocalManager.tipOpenLocal(activity as Context)
        LocalManager.startTraceService()
        LocalManager.startGather()
        //TODO 目前假设没有任何其他 情况发生 需要单独处理
//        LocalManager.dealRealLoc()
    }

    override fun onDestroy() {
//        LocalManager.stopRealLoc()
        super.onDestroy()
    }
}// Required empty public constructor
