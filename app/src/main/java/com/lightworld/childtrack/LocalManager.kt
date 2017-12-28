package com.lightworld.childtrack

import com.baidu.trace.LBSTraceClient
import com.baidu.trace.Trace

/**
 * Created by heyue on 2017/12/28.
 */
class LocalManager {
    companion object {
        lateinit var mTrace: Trace
        lateinit var mTraceClient: LBSTraceClient
    }

    fun initTrace() {

    }
}