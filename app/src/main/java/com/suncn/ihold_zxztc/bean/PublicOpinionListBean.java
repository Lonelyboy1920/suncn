package com.suncn.ihold_zxztc.bean;

import java.util.List;

public class PublicOpinionListBean extends BaseGlobal {

    /**
     * msg :
     * code : 200
     * data : {"pubList":[{"strPhotoPath":"/res/upload/mobile/publicationicon.png","strIssue":"2019年 第131期","strId":"d88f08ffade242b083159ee9fc9ce7e4","strType":"社情民意","strUrl":"/ios/PublicationView?strId=d88f08ffade242b083159ee9fc9ce7e4"},{"strPhotoPath":"/res/upload/mobile/publicationicon.png","strIssue":"2019年 第130期","strId":"c5cfc2e622f644e7ac6a8ed3584df38a","strType":"社情民意","strUrl":"/ios/PublicationView?strId=c5cfc2e622f644e7ac6a8ed3584df38a"},{"strPhotoPath":"/res/upload/mobile/publicationicon.png","strIssue":"2018年 第7期","strId":"1531192624996788","strType":"社情民意","strUrl":"/ios/PublicationView?strId=1531192624996788"}],"intCurPage":1,"intAllCount":0,"strSid":"800a088021994b7a8338c3625c59c31f","objList":[{"strTitle":"提交社情民意的数据2-暂存","strSourceName":"丁红","strId":"accc623422f2490d90a0ae02be298e36","strType":"社情民意","strStateName":"已采纳","strUrl":"/ios/InfoView?strId=accc623422f2490d90a0ae02be298e36","strPubDate":"2019-06-03"},{"strTitle":"提交社情民意的数据","strSourceName":"丁红","strId":"09451885cbb746a480099b6fce6b6cbd","strType":"社情民意","strStateName":"已采纳","strUrl":"/ios/InfoView?strId=09451885cbb746a480099b6fce6b6cbd","strPubDate":"2019-05-31"},{"strTitle":"关于南一环与明光路交口待转区划线建议","strSourceName":"丁红","strId":"1524129797519743","strType":"","strStateName":"审核中","strUrl":"/ios/InfoView?strId=1524129797519743","strPubDate":"2018-04-19"},{"strTitle":"合肥高校教室安装手机袋提升课堂品质建议","strSourceName":"丁红","strId":"1523170554217150","strType":"","strStateName":"审核中","strUrl":"/ios/InfoView?strId=1523170554217150","strPubDate":"2018-04-08"}],"intPageSize":20}
     */

    /**
     * pubList : [{"strPhotoPath":"/res/upload/mobile/publicationicon.png","strIssue":"2019年 第131期","strId":"d88f08ffade242b083159ee9fc9ce7e4","strType":"社情民意","strUrl":"/ios/PublicationView?strId=d88f08ffade242b083159ee9fc9ce7e4"},{"strPhotoPath":"/res/upload/mobile/publicationicon.png","strIssue":"2019年 第130期","strId":"c5cfc2e622f644e7ac6a8ed3584df38a","strType":"社情民意","strUrl":"/ios/PublicationView?strId=c5cfc2e622f644e7ac6a8ed3584df38a"},{"strPhotoPath":"/res/upload/mobile/publicationicon.png","strIssue":"2018年 第7期","strId":"1531192624996788","strType":"社情民意","strUrl":"/ios/PublicationView?strId=1531192624996788"}]
     * intCurPage : 1
     * intAllCount : 0
     * strSid : 800a088021994b7a8338c3625c59c31f
     * objList : [{"strTitle":"提交社情民意的数据2-暂存","strSourceName":"丁红","strId":"accc623422f2490d90a0ae02be298e36","strType":"社情民意","strStateName":"已采纳","strUrl":"/ios/InfoView?strId=accc623422f2490d90a0ae02be298e36","strPubDate":"2019-06-03"},{"strTitle":"提交社情民意的数据","strSourceName":"丁红","strId":"09451885cbb746a480099b6fce6b6cbd","strType":"社情民意","strStateName":"已采纳","strUrl":"/ios/InfoView?strId=09451885cbb746a480099b6fce6b6cbd","strPubDate":"2019-05-31"},{"strTitle":"关于南一环与明光路交口待转区划线建议","strSourceName":"丁红","strId":"1524129797519743","strType":"","strStateName":"审核中","strUrl":"/ios/InfoView?strId=1524129797519743","strPubDate":"2018-04-19"},{"strTitle":"合肥高校教室安装手机袋提升课堂品质建议","strSourceName":"丁红","strId":"1523170554217150","strType":"","strStateName":"审核中","strUrl":"/ios/InfoView?strId=1523170554217150","strPubDate":"2018-04-08"}]
     * intPageSize : 20
     */
    private List<PubListBean> pubList;
    private List<ObjListBean> objList;

    public List<PubListBean> getPubList() {
        return pubList;
    }

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public static class PubListBean {
        /**
         * strPhotoPath : /res/upload/mobile/publicationicon.png
         * strIssue : 2019年 第131期
         * strId : d88f08ffade242b083159ee9fc9ce7e4
         * strType : 社情民意
         * strUrl : /ios/PublicationView?strId=d88f08ffade242b083159ee9fc9ce7e4
         */

        private String strPhotoPath;
        private String strIssue;
        private String strId;
        private String strType;
        private String strUrl;

        public String getStrPhotoPath() {
            return strPhotoPath;
        }

        public String getStrIssue() {
            return strIssue;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrType() {
            return strType;
        }

        public String getStrUrl() {
            return strUrl;
        }
    }

    public static class ObjListBean {
        /**
         * strTitle : 提交社情民意的数据2-暂存
         * strSourceName : 丁红
         * strId : accc623422f2490d90a0ae02be298e36
         * strType : 社情民意
         * strStateName : 已采纳
         * strUrl : /ios/InfoView?strId=accc623422f2490d90a0ae02be298e36
         * strPubDate : 2019-06-03
         */

        private String strTitle;
        private String strSourceName;
        private String strId;
        private String strType;
        private String strStateName;
        private String strUrl;
        private String strPubDate;
        private String strJointMem;
        private String strTopicType;
        private String strContent;
        private String strTopicTypeName;

        public String getStrTopicTypeName() {
            return strTopicTypeName;
        }

        public String getStrContent() {
            return strContent;
        }

        public String getStrJointMem() {
            return strJointMem;
        }

        public String getStrTopicType() {
            return strTopicType;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrSourceName() {
            return strSourceName;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrType() {
            return strType;
        }

        public String getStrStateName() {
            return strStateName;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

    }
}
