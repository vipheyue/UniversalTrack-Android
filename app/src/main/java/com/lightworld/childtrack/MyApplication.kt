package com.lightworld.childtrack

import android.app.Application
import com.baidu.mapapi.SDKInitializer
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


/**
 * Created by heyue on 2017/12/25.
 */

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        Logger.addLogAdapter(AndroidLogAdapter())
        LocalManager.initTrace(this)
        SDKInitializer.initialize(this)


    }

    companion object {
        lateinit var INSTANCE: MyApplication
    }
}
