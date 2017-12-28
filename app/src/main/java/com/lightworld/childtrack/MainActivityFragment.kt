package com.lightworld.childtrack

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

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

    }


}
