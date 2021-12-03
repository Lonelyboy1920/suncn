package com.kotlin.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavin.giframe.utils.GIImageUtil
import com.kotlin.bean.DynamicListServletBean
import com.suncn.ihold_zxztc.R
import com.suncn.ihold_zxztc.utils.Utils
import java.net.URLDecoder

/**
 * @author :Sea
 * Date：2021-12-3 10:17
 * PackageName：com.kotlin.adapter
 * Desc：
 */
class CircleRvAdapter : BaseQuickAdapter<DynamicListServletBean.ObjListBean, BaseViewHolder>(R.layout.item_rv_circle) {
    override fun convert(holder: BaseViewHolder, item: DynamicListServletBean.ObjListBean) {
        holder.setText(R.id.tv_name, item.strPubUserName)
        GIImageUtil.loadImg(context, holder.getView(R.id.iv_img), Utils.formatFileUrl(context, item.strPhotoPath), 1)
        holder.setText(R.id.tv_position, item.strDuty)
        holder.setText(R.id.tv_date, item.strPubDate)
        holder.setText(R.id.tv_content, URLDecoder.decode(item.strContent, "utf-8"))
        var picList = item.picList
        if (picList.isNotEmpty() && picList.size > 1) {


        } else if (picList.isNotEmpty() && picList.size == 1) {

        } else {
            holder.setGone(R.id.mv_pic, true)
            holder.setGone(R.id.iv_simple_image, true)
        }
    }
}