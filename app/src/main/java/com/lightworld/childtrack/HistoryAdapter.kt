package com.lightworld.childtrack

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
class HistoryAdapter(layoutResId: Int, data: List<RemarkUserBean>?) : BaseQuickAdapter<RemarkUserBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: RemarkUserBean) {
        helper.setText(R.id.tv_userEntityName, item.entityName)
        helper.setText(R.id.tv_remark, item.remarkName)
//        helper.setOnClickListener(R.id.tv_history, view-> { xxx })
//        helper.setOnClickListener(R.id.tv_history) {
//            Log.i("xxxx",item.result)
//
//        }
    }
}
