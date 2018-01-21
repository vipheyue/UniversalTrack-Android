package com.lightworld.childtrack

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class IntroduceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduce)
        val mActionBar = supportActionBar
        mActionBar!!.setHomeButtonEnabled(true)
        mActionBar.setDisplayHomeAsUpEnabled(true)
        mActionBar.title = "应用介绍"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
