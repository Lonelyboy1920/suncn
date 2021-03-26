package com.kotlin.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author :Sea
 * Date：2021-3-26 18:03
 * PackageName：com.kotlin.rx
 * Desc：
 */
class ComputationMainScheduler <T> private constructor():BaseScheduler<T>(Schedulers.computation(),AndroidSchedulers.mainThread())