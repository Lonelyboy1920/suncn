package com.kotlin.activity.circle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.activity.KotlinBaseFragment
import com.kotlin.adapter.CircleRvAdapter
import com.kotlin.bean.DynamicListServletBean
import com.kotlin.bean.KotlinNewsInfoListServletBean
import com.kotlin.net.RetrofitManager
import com.suncn.ihold_zxztc.R
import kotlinx.android.synthetic.main.fragment_kotlin_circle.*
import java.util.HashMap

/**
 * @author :Sea
 * Date：2021-12-1 16:09
 * PackageName：com.kotlin.activity.hot
 * Desc：
 */
class KotlinCircleFragment : KotlinBaseFragment() {

    var mAdapter: CircleRvAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kotlin_circle, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var requestCallBack = object : RequestCallBack<Any> {
            override fun onSucess(data: Any?, sign: Int) {
                var loginBean = data as DynamicListServletBean
                Log.i("=================", loginBean.toString())
                mAdapter!!.setList(loginBean.dynamicList)
            }

            override fun onError(msg: String?) {
                Log.i("====================", msg)
            }
        }
        setTest(requestCallBack)
        getDynamicListServlet()

        mAdapter = CircleRvAdapter()
        rv_circle.adapter = mAdapter

    }


    /**
     * 请求列表数据
     */
    fun getDynamicListServlet() {
        var textParamMap = HashMap<String, String>()
        doRequestNormal(RetrofitManager().getInstance().getDynamicListServlet(textParamMap), 0)
    }
}