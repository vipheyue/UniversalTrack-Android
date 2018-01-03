package com.lightworld.childtrack

/**
 * Created by heyue on 2017/12/27.
 */
// 轨迹服务ID
var serviceId: Long = 116378
// 设备标识
var myTrackEntityName  by Preference(MyApplication.INSTANCE, "myTrackEntityName", "defaultTraceName")
var trackOtherEntityName by Preference(MyApplication.INSTANCE, "trackOtherEntityName", "trackOtherEntityName")
var lastQueryEntityName by Preference(MyApplication.INSTANCE, "lastQueryEntityName", "lastQueryEntityName")
// 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
var isNeedObjectStorage = false
// 定位周期(单位:秒)
var gatherInterval = 20
// 打包回传周期(单位:秒)
var packInterval = 30
var configAgreeLicense by Preference(MyApplication.INSTANCE, "configAgreeLicense", false)

var TRACK_ENTITY_NAME = "TRACK_ENTITY_NAME"
var isRegisterReceiver = false

var hisroty_query_dataCenter by Preference(MyApplication.INSTANCE, "hisroty_query_dataCenter","")