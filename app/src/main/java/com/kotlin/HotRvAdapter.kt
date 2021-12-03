package com.kotlin

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kotlin.bean.KotlinNewsInfoListServletBean
import com.suncn.ihold_zxztc.R

/**
 * @author :Sea
 * Date：2021-12-2 15:57
 * PackageName：com.kotlin
 * Desc：Kotlin 热点 列表 Adapter
 */
class HotRvAdapter : BaseQuickAdapter<KotlinNewsInfoListServletBean.ObjListBean, BaseViewHolder>(R.layout.item_kotlin_hot_list) {
    override fun convert(holder: BaseViewHolder, item: KotlinNewsInfoListServletBean.ObjListBean) {
        holder.setText(R.id.tv_title, item.strTitle)
        holder.setText(R.id.tv_department, item.strPubUnit)
        holder.setText(R.id.tv_time, item.strPubDate)
        if ("1".equals(item.strTopState)) {
            holder.setVisible(R.id.tv_top, true)
        } else {
            holder.setGone(R.id.tv_top, true)
        }
        if ("1".equals(item.strHotState)) {
            holder.setVisible(R.id.tv_hot, true)
        } else {
            holder.setGone(R.id.tv_hot, true)
        }
    }
}