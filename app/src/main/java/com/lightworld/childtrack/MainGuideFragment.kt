package com.lightworld.childtrack


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


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
        LocalManager.startTraceService()
//        LocalManager.dealWithData(activity as Context)
        LocalManager.dealRealLoc()
    }

    override fun onDestroy() {
        LocalManager.startTraceService()
        super.onDestroy()
    }
}// Required empty public constructor
