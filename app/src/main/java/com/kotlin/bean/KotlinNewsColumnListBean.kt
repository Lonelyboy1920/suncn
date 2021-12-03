package com.kotlin.bean

/**
 * @author :Sea
 * Date：2021-12-1 17:56
 * PackageName：com.kotlin.bean
 * Desc：Kotlin  热点栏目实体类
 */
data class KotlinNewsColumnListBean(val newsColumnList: List<NewsColumnBean>) {
    data class NewsColumnBean(val strId: String, val strName: String)
}