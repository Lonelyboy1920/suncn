package com.kotlin.activity.hot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hjq.toast.ToastUtils
import com.kotlin.adapter.HotRvAdapter
import com.kotlin.activity.KotlinBaseFragment
import com.kotlin.bean.KotlinNewsInfoListServletBean
import com.kotlin.net.RetrofitManager
import com.suncn.ihold_zxztc.R
import com.suncn.ihold_zxztc.utils.Utils
import kotlinx.android.synthetic.main.fragment_kotlin_hot_list.*
import java.util.HashMap

/**
 * @author :Sea
 * Date：2021-12-1 17:42
 * PackageName：com.kotlin.activity.hot
 * Desc： 新闻列表
 */
class KotlinHotListFragment : KotlinBaseFragment() {

    var mAdapter: HotRvAdapter? = null

    companion object {
        fun getInstance(strId: String): KotlinHotListFragment {
            var hotListFragment = KotlinHotListFragment()
            var bundle = Bundle()
            bundle.putString("strId", strId)
            hotListFragment.arguments = bundle
            return hotListFragment

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kotlin_hot_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var strId = arguments?.getString("strId")

        var requestCallBack = object : RequestCallBack<Any> {
            override fun onSucess(data: Any?, sign: Int) {
                var loginBean = data as KotlinNewsInfoListServletBean
                mAdapter!!.setList(loginBean.objList)
            }

            override fun onError(msg: String?) {
                Log.i("====================", msg)
            }
        }
        setTest(requestCallBack)

        mAdapter = HotRvAdapter()
        rv_hot_list.adapter = mAdapter

        mAdapter!!.setOnItemClickListener { adapter, view, position ->
            var item = mAdapter!!.getItem(position)
            ToastUtils.show(Utils.formatFileUrl(context, item.strUrl))
        }
        getNewsInfoListServlet(strId!!)
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