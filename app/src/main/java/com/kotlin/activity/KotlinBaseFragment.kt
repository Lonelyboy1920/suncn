package com.kotlin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gavin.giframe.utils.RxUtils
import com.kotlin.KotlinBaseActivity
import com.kotlin.bean.KotlinBaseResponse
import com.suncn.ihold_zxztc.rxhttp.RxDisposeManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * @author :Sea
 * Date：2021-12-1 15:52
 * PackageName：com.kotlin.activity
 * Desc：
 */
open class KotlinBaseFragment : Fragment() {

    open var requestCallBack: RequestCallBack<Any>? = null

    open fun setTest(requestCallBack: RequestCallBack<Any>) {
        this.requestCallBack = requestCallBack

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    interface RequestCallBack<T> {
        /**
         * 数据请求成功返回
         */
        fun onSucess(data: Any?, sign: Int)

        fun onError(msg: String?)
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
                    requestCallBack?.onError(throwable.message)
                })
        RxDisposeManager.get().add(javaClass.name, disposable) //添加当前类名(lin.frameapp.xxx)的dispose
    }


    public fun showActivity(activity: Activity, kcls: Class<Any>) {
        startActivity(Intent(activity, kcls))
    }

    public fun skipActivity(activity: Activity, kcls: Class<Any>) {
        startActivity(Intent(activity, kcls))
        activity.finish()
    }

}