package com.kotlin.activity.hot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.kotlin.KotlinBaseActivity
import com.kotlin.activity.KotlinBaseFragment
import com.kotlin.activity.KotlinMainActivity
import com.kotlin.bean.KotlinNewsColumnListBean
import com.kotlin.bean.LoginBean
import com.kotlin.net.RetrofitManager
import com.suncn.ihold_zxztc.R
import kotlinx.android.synthetic.main.fragment_kotlin_hot.*
import java.util.HashMap

/**
 * @author :Sea
 * Date：2021-12-1 16:09
 * PackageName：com.kotlin.activity.hot
 * Desc：Kotlin 热点
 */
class KotlinHotFragment : KotlinBaseFragment() {

    var mFragments: MutableList<Fragment>? = ArrayList()
    var mTitles: MutableList<String>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kotlin_hot, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var requestCallBack = object : RequestCallBack<Any> {
            override fun onSucess(data: Any?, sign: Int) {
                var loginBean = data as KotlinNewsColumnListBean
                loginBean.newsColumnList.forEach {
                    Log.i("===============", "栏目1111:${it.strName}\t")
//                    mTitles = listOf(it.strName)
                    mTitles!!.add(it.strName)
//                    mFragments = listOf(KotlinHotListFragment.getInstance(it.strId))
                    mFragments!!.add(KotlinHotListFragment.getInstance(it.strId))
                }
                viewPager.adapter = object : FragmentStatePagerAdapter(fragmentManager!!) {
                    override fun getCount(): Int {
                        return mFragments!!.size
                    }

                    override fun getItem(position: Int): Fragment {
                        return mFragments!!.get(position)
                    }


                    override fun getPageTitle(position: Int): CharSequence? {
                        return mTitles!!.get(position)
                    }
                }

            }

            override fun onError(msg: String?) {
                Log.i("====================", msg)
            }
        }
        setTest(requestCallBack)

        getColunm()

        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 1
    }


    fun getColunm() {
        var textParamMap = HashMap<String, String>()
        doRequestNormal(RetrofitManager().getInstance().getMyNewsColumnServlet(textParamMap), 0)
    }
}