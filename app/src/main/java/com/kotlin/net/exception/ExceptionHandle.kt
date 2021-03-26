package com.kotlin.net.exception

import android.util.Log
import com.google.gson.JsonParseException
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * @author :Sea
 * Date：2021-3-26 17:15
 * PackageName：com.kotlin.net.exception
 * Desc：
 */
class ExceptionHandle {

    companion object {
        var errorCode = ErrorStatus.UNKNOW_ERROR
        var errorMsg = "请求失败,请重试"

        fun handleException(e: Throwable): String {
            e.printStackTrace()
            if (e is SocketTimeoutException) { //网络请求超时
                Log.i("TAG", "网络连接异常: " + e.message)
                errorMsg = "网络连接异常"
                errorCode = ErrorStatus.NETWORK_ERROR
            } else if (e is ConnectException) {
                Log.i("TAG", "网络连接异常: " + e.message)
                errorMsg = "网络连接异常"
                errorCode = ErrorStatus.NETWORK_ERROR
            } else if (e is JsonParseException || e is JSONException || e is ParseException) {
                Log.i("TAG", "数据解析异常: " + e.message)
                errorMsg = "数据解析异常"
                errorCode = ErrorStatus.SERVER_ERROR
            } else if (e is ApiException) {//服务器返回的错误信息
                errorMsg = e.message.toString()
                errorCode = ErrorStatus.SERVER_ERROR
            } else if (e is UnknownHostException) {
                Log.i("TAG", "网络连接异常: " + e.message)
                errorMsg = "网络连接异常"
                errorCode = ErrorStatus.NETWORK_ERROR
            } else if (e is IllegalArgumentException) {
                errorMsg = "参数错误"
                errorCode = ErrorStatus.SERVER_ERROR
            } else {//未知错误
                try {
                    Log.i("TAG", "错误: " + e.message)
                } catch (e1: Exception) {
                    Log.i("TAG", "未知错误Debug调试 ")
                }

                errorMsg = "未知错误，可能抛锚了吧~"
                errorCode = ErrorStatus.UNKNOW_ERROR
            }

            return errorMsg
        }
    }
}