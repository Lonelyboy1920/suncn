package com.kotlin.activity.circle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.activity.KotlinBaseFragment
import com.kotlin.bean.KotlinNewsInfoListServletBean
import com.kotlin.net.RetrofitManager
import com.suncn.ihold_zxztc.R
import java.util.HashMap

/**
 * @author :Sea
 * Date：2021-12-1 16:09
 * PackageName：com.kotlin.activity.hot
 * Desc：
 */
class KotlinCircleFragment : KotlinBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kotlin_circle, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var requestCallBack = object : RequestCallBack<Any> {
            override fun onSucess(data: Any?, sign: Int) {
                var loginBean = data as KotlinNewsInfoListServletBean

            }

            override fun onError(msg: String?) {
                Log.i("====================", msg)
            }
        }
        setTest(requestCallBack)

    }


    /**
     * 请求列表数据
     */
    fun getNewsInfoListServlet(strId: String) {
        var textParamMap = HashMap<String, String>()
        textParamMap.put("strId", strId)
        doRequestNormal(RetrofitManager().getInstance().getNewsInfoListServlet(textParamMap), 0)
    }
}