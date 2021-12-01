package com.kotlin.activity.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.activity.KotlinBaseFragment
import com.suncn.ihold_zxztc.R

/**
 * @author :Sea
 * Date：2021-12-1 16:09
 * PackageName：com.kotlin.activity.application
 * Desc：应用
 */
class KotlinApplicationFragment : KotlinBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kotlin_application, null)
    }


}