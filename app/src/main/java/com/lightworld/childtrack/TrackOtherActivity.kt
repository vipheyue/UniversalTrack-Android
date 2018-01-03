package com.lightworld.childtrack

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import com.lightworld.childtrack.helper.ClipboardManagerHelper
import com.lightworld.childtrack.helper.HistoryQueryTable
import io.github.xudaojie.qrcodelib.CaptureActivity
import kotlinx.android.synthetic.main.activity_track_other.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class TrackOtherActivity : AppCompatActivity() {
    lateinit var dataCenter: ArrayList<RemarkUserBean>
    var historyAdapter: HistoryAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_other)
        initView()
    }


    private val REQUEST_QR_CODE: Int = 111

    private fun initView() {
        val mActionBar = supportActionBar
        mActionBar!!.setHomeButtonEnabled(true)
        mActionBar.setDisplayHomeAsUpEnabled(true)
        mActionBar.title = "追踪他人"

        rtv_scan.setOnClickListener {
            //启动扫码页面
            val i = Intent(this, CaptureActivity::class.java)
            startActivityForResult(i, REQUEST_QR_CODE)
        }
        rtv_startTrack.setOnClickListener {
            val entityName = et_input_name.text.toString().trim()
            val splitSymbol = ClipboardManagerHelper.splitSymbol(entityName)
            val remarkUserBean = RemarkUserBean(splitSymbol)
//            historyAdapter!!.addData(0, remarkUserBean)
            HistoryQueryTable.saveData(remarkUserBean)//存入数据库
            lastQueryEntityName = splitSymbol
            startActivity<TrackMapActivity>()
            finish()
        }
        //读取数据中心
        dataCenter = HistoryQueryTable.getAllData()


        historyAdapter = HistoryAdapter(R.layout.item_query_history, dataCenter)
        historyAdapter!!.setOnItemClickListener { adapter, view, position ->
            toast("长按可备注哦")
            val remarkUserBean = dataCenter.get(position)
//            historyAdapter!!.remove(position)
//            historyAdapter!!.addData(0, remarkUserBean)
            HistoryQueryTable.saveData(remarkUserBean)
            lastQueryEntityName = remarkUserBean.entityName
            startActivity<TrackMapActivity>()
            finish()

        }
        historyAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            //弹出一个输入框 确定后更新recyclerView
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.dialog_input, null)
            val et_remark = view.findViewById<EditText>(R.id.et_remark)
            builder.setTitle("添加备注名")
                    .setView(view)
                    .setNegativeButton("取消") { dialog, which ->
                    }
                    .setPositiveButton("确认") { dialog, which ->
                        val remarkUserBean = dataCenter.get(position)
                        remarkUserBean.remarkName = et_remark.text.trim().toString()
                        historyAdapter!!.remove(position)
                        historyAdapter!!.addData(0, remarkUserBean)
                        HistoryQueryTable.saveData(remarkUserBean)
                    }

            builder.create().show()
            return@setOnItemLongClickListener true
        }
        recyclerView_history.setLayoutManager(LinearLayoutManager(this))
        recyclerView_history.setAdapter(historyAdapter)
        ClipboardManagerHelper.discernSymbol(this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_QR_CODE
                && data != null) {
            val entrity = data.getStringExtra("result")
//            Toast.makeText(this@TrackOtherActivity, result, Toast.LENGTH_SHORT).show()
            val remarkUserBean = RemarkUserBean(entrity)
            HistoryQueryTable.saveData(remarkUserBean)
//            historyAdapter!!.addData(0, remarkUserBean)
            lastQueryEntityName =entrity
            startActivity<TrackMapActivity>()
            finish()

        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}
