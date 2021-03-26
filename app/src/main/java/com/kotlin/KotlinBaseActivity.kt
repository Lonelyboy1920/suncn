package com.kotlin

import androidx.appcompat.app.AppCompatActivity
import com.gavin.giframe.http.BaseResponse
import com.gavin.giframe.utils.RxUtils
import com.suncn.ihold_zxztc.bean.LoginBean
import com.suncn.ihold_zxztc.rxhttp.RxDisposeManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author :Sea
 * Date：2021-3-26 10:42
 * PackageName：com.kotlin
 * Desc：
 */
open class KotlinBaseActivity : AppCompatActivity() {

    lateinit var requestCallBack: RequestCallBack<Any>

    fun doRequestNormal(observable: Observable<BaseResponse<Any>>, sign: Int) {
        var disposable: Disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.schedulersTransformer())
                .subscribe({
                    it.toString()
                    requestCallBack.onSucess(it, sign)
                }, {
                    it.printStackTrace()
                })


        RxDisposeManager.get().add(localClassName, disposable)
    }


    interface RequestCallBack<T> {
        /**
         * 数据请求成功返回
         */
        fun onSucess(data: T, sign: Int)
    }


}