package com.kotlin

import android.Manifest
import android.os.Bundle
import android.util.Log
import com.gavin.giframe.authcode.GIAESOperator
import com.gavin.giframe.utils.GILogUtil
import com.gavin.giframe.utils.GIPhoneUtils
import com.gavin.giframe.utils.GISharedPreUtil
import com.kotlin.bean.KotlinBaseResponse
import com.kotlin.bean.LoginBean
import com.kotlin.net.RetrofitManager
import com.suncn.ihold_zxztc.R
import com.suncn.ihold_zxztc.view.CustomVideoView
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import java.util.*

/**
 * @author :Sea
 * Date：2021-3-26 10:01
 * PackageName：com.kotlin
 * Desc： 闪屏页
 */
class KotlinSplashActivity : KotlinBaseActivity() {
    var DELAY_TYPE: Long = 3000L
    lateinit var videoView: CustomVideoView

    override fun onResume() {
        super.onResume()
        Log.i("===============", "SplashOnRes")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_kotlin)

        var requestCallBack = object : RequestCallBack<Any> {
            override fun onSucess(data: Any?, sign: Int) {
                var loginBean = data as LoginBean
                Log.i("===============", "姓名:${loginBean.strName}\t 用户角色:${loginBean.intUserRole}")
            }
        }
        setTest(requestCallBack)
        Log.i("===============", "SplashOnCre")
        doLogin()

    }

    /**
     * 登陆操作
     */
    private fun doLogin() {
        HiPermission.create(this).checkSinglePermission(Manifest.permission.READ_PHONE_STATE, object : PermissionCallback {
            override fun onGuarantee(permisson: String, position: Int) { // 同意/已授权
                val deviceCode = GIPhoneUtils.getSerialNumber(this@KotlinSplashActivity) // 获取设备号
                GISharedPreUtil.setValue(this@KotlinSplashActivity, "deviceCode", deviceCode)
                var textParamMap = HashMap<String, String>()
                textParamMap.put("strUdid", deviceCode)
                // 用户名、密码登录方式
                textParamMap.put("strUsername", "zhaoliying")
                textParamMap.put("strPassword", GIAESOperator.getInstance().encrypt("abcd@12345"))
                textParamMap.put("strVersion", GIPhoneUtils.getAppVersionCode(this@KotlinSplashActivity).toString())
                doRequestNormal(RetrofitManager().getInstance().doLogin(textParamMap), 0)
            }

            override fun onClose() { // 用户关闭权限申请
                GILogUtil.e("onClose")
            }

            override fun onFinish() { // 所有权限申请完成
                GILogUtil.e("onFinish")
            }

            override fun onDeny(permisson: String, position: Int) { // 拒绝
                GILogUtil.e("onDeny")
            }
        })
    }
}