package com.kotlin.bean

/**
 * @author :Sea
 * Date：2021-12-3 10:19
 * PackageName：com.kotlin.bean
 * Desc：
 */
data class DynamicListServletBean(val dynamicList: List<ObjListBean>) {
    data class ObjListBean(val strId: String,
                           val strContent: String,
                           val picList: List<PicListBean>,
                           val strPubUserId: String,
                           val strPhotoPath: String,
                           val strPubUserName: String,
                           val strDuty: String,
                           val strPubDate: String) {
        data class PicListBean(val strPrimaryImagePath: String,
                               val strImagePath: String,
                               val strThumbPath: String,
                               val strFileId: String,
                               val strVideoImagePath: String,
                               val strFileType: String)
    }
}
