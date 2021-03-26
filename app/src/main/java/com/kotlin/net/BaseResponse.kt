package com.kotlin.net

/**
 * @author :Sea
 * Date：2021-3-26 17:07
 * PackageName：com.kotlin.net
 * Desc： 返回数据封装
 */
class BaseResponse<T>(val code: Int, val msg: String, val strSid: String, val data: T)