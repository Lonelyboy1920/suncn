package com.kotlin.bean

/**
 * @author :Sea
 * Date：2021-12-2 15:49
 * PackageName：com.kotlin.bean
 * Desc：Kotlin 热点列表实体类
 */
data class KotlinNewsInfoListServletBean(val objList: List<ObjListBean>) {


    data class ObjListBean(val strId: String,
                           val strTitle: String,
                           val strUrl: String,
                           val strPubUnit: String,
                           val strPubDate: String,
                           val strTopState: String,
                           val strHotState: String)
}