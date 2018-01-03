package com.lightworld.childtrack

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class TrackMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val entityName = intent.getStringExtra(TRACK_ENTITY_NAME)
//        lastQueryEntityName = entityName
        setContentView(R.layout.activity_map_track)

        val mActionBar = supportActionBar
        mActionBar!!.setHomeButtonEnabled(true)
        mActionBar.setDisplayHomeAsUpEnabled(true)
        mActionBar.title = "查看轨迹"

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
