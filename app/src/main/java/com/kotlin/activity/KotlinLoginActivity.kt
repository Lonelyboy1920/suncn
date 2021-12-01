package com.kotlin.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.gavin.giframe.authcode.GIAESOperator
import com.gavin.giframe.utils.GILogUtil
import com.gavin.giframe.utils.GIPhoneUtils
import com.gavin.giframe.utils.GISharedPreUtil
import com.hjq.toast.ToastUtils
import com.kotlin.KotlinBaseActivity
import com.kotlin.bean.LoginBean
import com.kotlin.net.RetrofitManager
import com.suncn.ihold_zxztc.R
import com.suncn.ihold_zxztc.activity.MainActivity
import kotlinx.android.synthetic.main.activity_kotlin_login.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import java.util.HashMap

/**
 * @author :Sea
 * Date：2021-11-30 17:41
 * PackageName：com.kotlin.activity
 * Desc：Kotlin 登录
 */
class KotlinLoginActivity : KotlinBaseActivity() {

    var strUserName = ""
    var strPwd: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_login)
        var requestCallBack = object : RequestCallBack<Any> {
            override fun onSucess(data: Any?, sign: Int) {
                var loginBean = data as LoginBean
                Log.i("===============", "姓名:${loginBean.strName}\t 用户角色:${loginBean.intUserRole}")
                showActivity(this@KotlinLoginActivity, KotlinMainActivity().javaClass)
            }

            override fun onError(msg: String?) {
                Log.i("====================", msg)
            }
        }

        tv_back.setOnClickListener { view -> finish() }


        iv_login.setOnClickListener { view ->

            strUserName = et_username.text.toString().trim()
            strPwd = et_password.text.toString().trim()
            if (strUserName.isEmpty()) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                et_username.requestFocus()
                return@setOnClickListener
            }
            if (strPwd.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                et_password.requestFocus()
                return@setOnClickListener
            }
            doLogin()
        }

        setTest(requestCallBack)

    }


    /**
     * 登陆操作
     */
    private fun doLogin() {
        HiPermission.create(this).checkSinglePermission(Manifest.permission.READ_PHONE_STATE, object : PermissionCallback {
            override fun onGuarantee(permisson: String, position: Int) { // 同意/已授权
                val deviceCode = GIPhoneUtils.getSerialNumber(this@KotlinLoginActivity) // 获取设备号
                GISharedPreUtil.setValue(this@KotlinLoginActivity, "deviceCode", deviceCode)
                var textParamMap = HashMap<String, String>()
                textParamMap.put("strUdid", deviceCode)
                // 用户名、密码登录方式
                textParamMap.put("strUsername", strUserName)
                textParamMap.put("strPassword", GIAESOperator.getInstance().encrypt(strPwd))
                textParamMap.put("strVersion", GIPhoneUtils.getAppVersionCode(this@KotlinLoginActivity).toString())
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