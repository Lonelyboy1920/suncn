package com.kotlin

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.WindowManager
import com.gavin.giframe.http.BaseResponse
import com.gavin.giframe.utils.GILogUtil
import com.gavin.giframe.utils.GIPhoneUtils
import com.gavin.giframe.utils.GISharedPreUtil
import com.gavin.giframe.utils.RxUtils
import com.kotlin.net.RetrofitManager
import com.kotlin.net.exception.ExceptionHandle
import com.suncn.ihold_zxztc.ApiManager
import com.suncn.ihold_zxztc.R
import com.suncn.ihold_zxztc.rxhttp.RxDisposeManager
import com.suncn.ihold_zxztc.view.CustomVideoView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
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
//        if (resources.getBoolean(R.bool.IS_OPEN_SPLASH_ROBOT) and !GISharedPreUtil.getString(this, "strSplashUrl").isNullOrEmpty()) {
//            videoView = CustomVideoView(this)
//            setContentView(videoView)
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            var params = window.attributes
//            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//            window.attributes = params
//        }
        Log.i("===============","SplashOnRes")
        doLogin()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_splash_kotlin)

        Log.i("===============","SplashOnCre")
        object : RequestCallBack<Any> {
            override fun onSucess(data: Any, sign: Int) {

            }
        }



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
                textParamMap.put("intType", "20")
                textParamMap.put("strVersion", GIPhoneUtils.getAppVersionCode(this@KotlinSplashActivity).toString())
//                doRequestNormal(ApiManager.getInstance().doLogin(textParamMap), 0)

                RetrofitManager.service.doLogin(textParamMap).subscribe({
                    Log.i("=======================", it.toString())
                }, {
                    Log.i("=======================", ExceptionHandle.handleException(it) + ExceptionHandle.errorCode)
                })
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