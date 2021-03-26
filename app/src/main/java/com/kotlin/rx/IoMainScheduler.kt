package com.kotlin.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author :Sea
 * Date：2021-3-26 18:05
 * PackageName：com.kotlin.rx
 * Desc：
 */
class IoMainScheduler<T> : BaseScheduler<T>(Schedulers.io(), AndroidSchedulers.mainThread())