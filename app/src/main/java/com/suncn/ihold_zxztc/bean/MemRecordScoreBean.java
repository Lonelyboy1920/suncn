package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.List;

public class MemRecordScoreBean extends BaseGlobal {


    /**
     * dbAllScore : 874
     * abilityAnalysis : {"radarIndicatorList":[{"strTypeName":"提交提案","dbScore":10,"dbScoreRatio":0.01,"examList":[{"strExamItemName":"测试规则1","dbScore":10,"strScoreTypeName":"基础分"}]},{"strTypeName":"社情民意","dbScore":828,"dbScoreRatio":0.95,"examList":[{"strExamItemName":"国家领导批示","dbScore":800,"strScoreTypeName":"基础分"},{"strExamItemName":"第一份采编社情民意","dbScore":10,"strScoreTypeName":"基础分"},{"strExamItemName":"每两条未采编","dbScore":10,"strScoreTypeName":"基础分"},{"strExamItemName":"第一份未采编信息","dbScore":6,"strScoreTypeName":"基础分"},{"strExamItemName":"第二份采编","dbScore":2,"strScoreTypeName":"基础分"}]},{"strTypeName":"参加会议","dbScore":11,"dbScoreRatio":0.01,"examList":[{"strExamItemName":"第一次参加任意会议","dbScore":11,"strScoreTypeName":"基础分"}]},{"strTypeName":"参加活动","dbScore":21,"dbScoreRatio":0.02,"examList":[{"strExamItemName":"每参加一次活动","dbScore":21,"strScoreTypeName":"基础分"}]},{"strTypeName":"会议发言","dbScore":4,"dbScoreRatio":0,"examList":[{"strExamItemName":"2112","dbScore":2,"strScoreTypeName":"基础分"},{"strExamItemName":"2112","dbScore":2,"strScoreTypeName":"基础分"}]},{"strTypeName":"额外加分","dbScore":0,"dbScoreRatio":0,"examList":[]}],"strMaxTypeName":"社情民意","strSectorName":"界别","intSectorRank":1,"strMinTypeName":"额外加分"}
     * scoreRankList : [{"strRankName":"总排行","rankList":[{"strName":"委员02","dbAllScore":874,"intScoreRank":2},{"strName":"程震","dbAllScore":3354,"intScoreRank":1},{"strName":"老干部","dbAllScore":831,"intScoreRank":3},{"strName":"陈林(委员)","dbAllScore":831,"intScoreRank":4},{"strName":"胡伟","dbAllScore":814,"intScoreRank":5}]},{"strRankName":"专委会","rankList":[{"strName":"委员02","dbAllScore":874,"intScoreRank":2},{"strName":"程震","dbAllScore":3354,"intScoreRank":1},{"strName":"老干部","dbAllScore":831,"intScoreRank":3},{"strName":"陈林(委员)","dbAllScore":831,"intScoreRank":4},{"strName":"胡伟","dbAllScore":814,"intScoreRank":5}]},{"strRankName":"界别","rankList":[{"strName":"委员02","dbAllScore":874,"intScoreRank":1},{"strName":"老干部","dbAllScore":831,"intScoreRank":2}]},{"strRankName":"党派","rankList":[{"strName":"委员02","dbAllScore":874,"intScoreRank":1}]}]
     */

    private String strAllScore;
    private AbilityAnalysisBean abilityAnalysis;
    private List<ScoreRankListBean> scoreRankList;
    private String scoreProportionShow;//计分规则

    public String getScoreProportionShow() {
        return scoreProportionShow;
    }

    public String getDbAllScore() {
        return strAllScore;
    }

    public AbilityAnalysisBean getAbilityAnalysis() {
        return abilityAnalysis;
    }

    public void setAbilityAnalysis(AbilityAnalysisBean abilityAnalysis) {
        this.abilityAnalysis = abilityAnalysis;
    }

    public List<ScoreRankListBean> getScoreRankList() {
        return scoreRankList;
    }

    public void setScoreRankList(List<ScoreRankListBean> scoreRankList) {
        this.scoreRankList = scoreRankList;
    }

    public static class AbilityAnalysisBean {
        /**
         * radarIndicatorList : [
         * {"strTypeName":"提交提案","dbScore":10,"dbScoreRatio":0.01,
         * "examList":[{"strExamItemName":"测试规则1","dbScore":10,"strScoreTypeName":"基础分"}]},
         * {"strTypeName":"社情民意","dbScore":828,"dbScoreRatio":0.95,
         * "examList":[{"strExamItemName":"国家领导批示","dbScore":800,"strScoreTypeName":"基础分"},{"strExamItemName":"第一份采编社情民意","dbScore":10,"strScoreTypeName":"基础分"},{"strExamItemName":"每两条未采编","dbScore":10,"strScoreTypeName":"基础分"},{"strExamItemName":"第一份未采编信息","dbScore":6,"strScoreTypeName":"基础分"},{"strExamItemName":"第二份采编","dbScore":2,"strScoreTypeName":"基础分"}]},
         * {"strTypeName":"参加会议","dbScore":11,"dbScoreRatio":0.01,"examList":[{"strExamItemName":"第一次参加任意会议","dbScore":11,"strScoreTypeName":"基础分"}]},{"strTypeName":"参加活动","dbScore":21,"dbScoreRatio":0.02,"examList":[{"strExamItemName":"每参加一次活动","dbScore":21,"strScoreTypeName":"基础分"}]},{"strTypeName":"会议发言","dbScore":4,"dbScoreRatio":0,"examList":[{"strExamItemName":"2112","dbScore":2,"strScoreTypeName":"基础分"},{"strExamItemName":"2112","dbScore":2,"strScoreTypeName":"基础分"}]},{"strTypeName":"额外加分","dbScore":0,"dbScoreRatio":0,"examList":[]}]
         * strMaxTypeName : 社情民意
         * strSectorName : 界别
         * intSectorRank : 1
         * strMinTypeName : 额外加分
         */

        private String strRankShow;
        private String strMaxTypeName;
        private String strSectorName;
        private int intSectorRank;
        private String strMinTypeName;
        private List<RadarIndicatorListBean> radarIndicatorList;

        public String getStrRankShow() {
            return strRankShow;
        }

        public String getStrMaxTypeName() {
            return strMaxTypeName;
        }

        public void setStrMaxTypeName(String strMaxTypeName) {
            this.strMaxTypeName = strMaxTypeName;
        }

        public String getStrSectorName() {
            return strSectorName;
        }

        public void setStrSectorName(String strSectorName) {
            this.strSectorName = strSectorName;
        }

        public int getIntSectorRank() {
            return intSectorRank;
        }

        public void setIntSectorRank(int intSectorRank) {
            this.intSectorRank = intSectorRank;
        }

        public String getStrMinTypeName() {
            return strMinTypeName;
        }

        public void setStrMinTypeName(String strMinTypeName) {
            this.strMinTypeName = strMinTypeName;
        }

        public List<RadarIndicatorListBean> getRadarIndicatorList() {
            return radarIndicatorList;
        }

        public void setRadarIndicatorList(List<RadarIndicatorListBean> radarIndicatorList) {
            this.radarIndicatorList = radarIndicatorList;
        }

        public static class RadarIndicatorListBean {
            /**
             * strTypeName : 提交提案
             * dbScore : 10
             * dbScoreRatio : 0.01
             * examList : [{"strExamItemName":"测试规则1","dbScore":10,"strScoreTypeName":"基础分"}]
             */

            private String strTypeName;
            private String strScore;
            private String strScoreRatio;
            private List<ExamListBean> examList;


            public String getStrTypeName() {
                return strTypeName;
            }

            public void setStrTypeName(String strTypeName) {
                this.strTypeName = strTypeName;
            }

            public String getDbScore() {
                return strScore;
            }

            public String getDbScoreRatio() {
                return strScoreRatio;
            }

            public List<ExamListBean> getExamList() {
                return examList;
            }

            public void setExamList(List<ExamListBean> examList) {
                this.examList = examList;
            }

            public static class ExamListBean {
                /**
                 * strExamItemName : 测试规则1
                 * dbScore : 10
                 * strScoreTypeName : 基础分
                 */

                private String strExamItemName;
                private String strScore;
                private String strScoreTypeName;

                public String getStrExamItemName() {
                    return strExamItemName;
                }

                public void setStrExamItemName(String strExamItemName) {
                    this.strExamItemName = strExamItemName;
                }

                public String getDbScore() {
                    return strScore;
                }

                public String getStrScoreTypeName() {
                    return strScoreTypeName;
                }

                public void setStrScoreTypeName(String strScoreTypeName) {
                    this.strScoreTypeName = strScoreTypeName;
                }
            }
        }
    }

    public static class ScoreRankListBean implements Serializable {
        /**
         * strRankName : 总排行
         * rankList : [{"strName":"委员02","dbAllScore":874,"intScoreRank":2},{"strName":"程震","dbAllScore":3354,"intScoreRank":1},{"strName":"老干部","dbAllScore":831,"intScoreRank":3},{"strName":"陈林(委员)","dbAllScore":831,"intScoreRank":4},{"strName":"胡伟","dbAllScore":814,"intScoreRank":5}]
         */

        private String strRankName;
        private String strRankType;
        private List<RankListBean> rankList;

        public String getStrRankType() {
            return strRankType;
        }

        public String getStrRankName() {
            return strRankName;
        }

        public void setStrRankName(String strRankName) {
            this.strRankName = strRankName;
        }

        public List<RankListBean> getRankList() {
            return rankList;
        }

        public void setRankList(List<RankListBean> rankList) {
            this.rankList = rankList;
        }

        public static class RankListBean implements Serializable {
            /**
             * strName : 委员02
             * dbAllScore : 874
             * intScoreRank : 2
             */

            private String strName;
            private String strAllScore;
            private int intScoreRank;

            public String getStrName() {
                return strName;
            }

            public void setStrName(String strName) {
                this.strName = strName;
            }

            public String getDbAllScore() {
                return strAllScore;
            }

            public int getIntScoreRank() {
                return intScoreRank;
            }

            public void setIntScoreRank(int intScoreRank) {
                this.intScoreRank = intScoreRank;
            }
        }
    }
}
