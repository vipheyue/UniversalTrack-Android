package com.lightworld.childtrack

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.toast

class TrackMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_track)
        val entityName = intent.getStringExtra(TRACK_ENTITY_NAME)
        lastQueryEntityName=entityName
        toast(entityName)
    }
}
