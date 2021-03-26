package com.kotlin.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author :Sea
 * Date：2021-3-26 18:08
 * PackageName：com.kotlin.rx
 * Desc：
 */
class SingleMainScheduler<T> private constructor() : BaseScheduler<T>(Schedulers.single(), AndroidSchedulers.mainThread()) {
}