package com.kotlin.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.kotlin.KotlinBaseActivity
import com.kotlin.activity.application.KotlinApplicationFragment
import com.kotlin.activity.circle.KotlinCircleFragment
import com.kotlin.activity.duty.KotlinMemDutyFragment
import com.kotlin.activity.hot.KotlinHotFragment
import com.kotlin.activity.message.KotlinMessageFragment
import com.suncn.ihold_zxztc.R
import com.suncn.ihold_zxztc.view.NormalItemView
import kotlinx.android.synthetic.main.activity_kotlin_main.*
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener

/**
 * @author :Sea
 * Date：2021-12-1 14:57
 * PackageName：com.kotlin.activity
 * Desc： Kotlin 主界面
 */
class KotlinMainActivity : KotlinBaseActivity() {
    var currentFragment: Fragment? = null
    var mFragments: List<Fragment>? = null

    override fun onResume() {
        super.onResume()
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_main)

        mFragments = listOf(KotlinMessageFragment(), KotlinCircleFragment(), KotlinHotFragment(), KotlinMemDutyFragment(), KotlinApplicationFragment())
        initBottomTab()
    }

    fun initBottomTab() {
        var navigationController: NavigationController

        navigationController = pager_bottom_tab.custom()
                .addItem(newItem(R.mipmap.bottom_message, R.mipmap.bottom_message_hover, "消息"))
                .addItem(newItem(R.mipmap.bottom_circle, R.mipmap.bottom_circle_hover, "圈子"))
                .addItem(newItem(R.mipmap.bottom_hot, R.mipmap.bottom_hot_hover, "热点"))
                .addItem(newItem(R.mipmap.bottom_duty, R.mipmap.bottom_duty_hover, "履职"))
                .addItem(newItem(R.mipmap.bottom_func, R.mipmap.bottom_func_hover, "应用"))
                .build()

        navigationController.addTabItemSelectedListener(object : OnTabItemSelectedListener {
            override fun onSelected(index: Int, old: Int) {
                switchFragment(mFragments!!.get(index))
            }

            override fun onRepeat(index: Int) {
                Log.i("================", "index:${index}")
            }

        })

        navigationController.setSelect(2)
    }

    fun newItem(drawable: Int, checkedDrawable: Int, text: String): BaseTabItem {
        var normalItemView: NormalItemView = NormalItemView(this)
        normalItemView.initialize(drawable, checkedDrawable, text)
        normalItemView.setTextDefaultColor(resources.getColor(R.color.font_content))
        normalItemView.setTextCheckedColor(resources.getColor(R.color.view_head_bg))
        return normalItemView
    }

    fun switchFragment(targetFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        if (!targetFragment.isAdded) {
            if (currentFragment == null) {
                transaction.add(R.id.frameLayout, targetFragment)
            } else {
                transaction.hide(currentFragment!!).add(R.id.frameLayout, targetFragment)

            }
            transaction.commitAllowingStateLoss()
            currentFragment = targetFragment
        } else {
            transaction.hide(currentFragment!!).show(targetFragment)
            transaction.commitAllowingStateLoss()
            currentFragment = targetFragment
            targetFragment.onResume()
        }
    }
}