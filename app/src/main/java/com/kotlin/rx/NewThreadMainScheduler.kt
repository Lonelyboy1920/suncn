package com.kotlin.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author :Sea
 * Date：2021-3-26 18:06
 * PackageName：com.kotlin.rx
 * Desc：
 */
class NewThreadMainScheduler<T> private constructor():BaseScheduler<T>(Schedulers.newThread(),AndroidSchedulers.mainThread()){
}