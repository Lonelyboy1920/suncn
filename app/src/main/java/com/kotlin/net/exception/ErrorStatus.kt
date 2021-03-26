package com.kotlin.net.exception

/**
 * @author :Sea
 * Date：2021-3-26 17:13
 * PackageName：com.kotlin.net.exception
 * Desc：
 */
object ErrorStatus {
    /**
     * 响应成功
     */
    @JvmField
    var SUCCESS = 0

    /**
     * 未知错误
     */
    @JvmField
    val UNKNOW_ERROR = 1002;

    /**
     * 服务器内部错误
     */
    @JvmField
    val SERVER_ERROR = 1003

    /**
     * 网络连接超时
     */
    @JvmField
    val NETWORK_ERROR = 1004

    /**
     * API解析异常（或者第三方数据结构更改）等其他异常
     */
    @JvmField
    val API_ERROR = 1005
}