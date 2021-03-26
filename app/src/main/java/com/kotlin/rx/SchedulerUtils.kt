package com.kotlin.rx

/**
 * @author :Sea
 * Date：2021-3-26 18:11
 * PackageName：com.kotlin.rx
 * Desc：
 */
object SchedulerUtils {
    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
}