package com.lightworld.childtrack

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger



/**
 * Created by heyue on 2017/12/25.
 */

class MyApplication : Application() {
    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    companion object {
        private lateinit var INSTANCE: MyApplication

        fun get(): MyApplication = INSTANCE
    }
}
