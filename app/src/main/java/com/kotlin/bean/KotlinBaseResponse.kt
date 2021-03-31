package com.kotlin.bean

/**
 * @author :Sea
 * Date：2021-3-30 14:24
 * PackageName：com.kotlin.bean
 * Desc：
 */
data class KotlinBaseResponse<T>(var code: Int, var msg: String, var strSid: String, var data: T){

}

