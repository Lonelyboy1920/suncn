package com.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gavin.giframe.utils.RxUtils
import com.kotlin.bean.KotlinBaseResponse
import com.kotlin.bean.LoginBean
import com.suncn.ihold_zxztc.rxhttp.RxDisposeManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * @author :Sea
 * Date：2021-3-26 10:42
 * PackageName：com.kotlin
 * Desc：
 */
open class KotlinBaseActivity : AppCompatActivity() {

    open var requestCallBack: RequestCallBack<Any>? = null

    open fun setTest(requestCallBack: RequestCallBack<Any>) {
        this.requestCallBack = requestCallBack

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    interface RequestCallBack<T> {
        /**
         * 数据请求成功返回
         */
        fun onSucess(data: Any?, sign: Int)
    }

    /**
     * 一般请求，返回数据带有body
     */
    open fun <T> doRequestNormal(observable: Observable<KotlinBaseResponse<T>>, sign: Int) {
        val disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .subscribe(Consumer { tBaseResponse ->
                    var kotlinBaseResponse = tBaseResponse as KotlinBaseResponse<T>
                    requestCallBack?.onSucess(kotlinBaseResponse.data, sign)
                }, Consumer { throwable ->
                    throwable.printStackTrace()
                })
        RxDisposeManager.get().add(javaClass.name, disposable) //添加当前类名(lin.frameapp.xxx)的dispose
    }


}