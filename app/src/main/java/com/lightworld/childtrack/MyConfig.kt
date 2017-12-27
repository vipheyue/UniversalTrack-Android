package com.lightworld.childtrack

/**
 * Created by heyue on 2017/12/27.
 */
// 轨迹服务ID
var serviceId: Long = 116378
// 设备标识
var entityName = "myTrace"
// 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
var isNeedObjectStorage = false
// 定位周期(单位:秒)
var gatherInterval = 5
// 打包回传周期(单位:秒)
var packInterval = 10