package com.lightworld.childtrack.helper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lightworld.childtrack.RemarkUserBean
import com.lightworld.childtrack.hisroty_query_dataCenter

/**
 * Created by heyue on 2018/1/3.
 */
object HistoryQueryTable {
    fun saveData(remarkUserBean: RemarkUserBean) {
        var userId = remarkUserBean.entityName
        var lastRemark = remarkUserBean.remarkName
        var dataCenter = getAllData()//读取所有 数据
        try {        //遍历找到userId 如果有备注就找出来
            var oldBean = dataCenter.find { userId.equals(it.entityName) }
            oldBean?.let {
                if (it.remarkName.isNotEmpty() && lastRemark.isEmpty()) {//有老备注,没有新备注
                    lastRemark = it.remarkName
                }
            }
            //删除老元素
            dataCenter.remove(oldBean)
        } catch (e: Exception) {
        }

        //存入历史数据表
        dataCenter.add(0, RemarkUserBean(userId, lastRemark))
        val linkedHashSet = LinkedHashSet<RemarkUserBean>(dataCenter)//新元素在最前面,老元素自动过滤
        val listWithoutDuplicateElements = ArrayList<RemarkUserBean>(linkedHashSet.take(100))
        hisroty_query_dataCenter = Gson().toJson(listWithoutDuplicateElements)

    }

    fun getAllData(): ArrayList<RemarkUserBean> {
        var dataCenter = ArrayList<RemarkUserBean>()
        try {
            dataCenter.addAll(Gson().fromJson(hisroty_query_dataCenter, object : TypeToken<List<RemarkUserBean>>() {}.type))
        } catch (e: Exception) {
            //如果 报异常就是没有数据
        }
        return dataCenter
    }
}