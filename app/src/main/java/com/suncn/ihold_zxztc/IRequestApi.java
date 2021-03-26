package com.suncn.ihold_zxztc;

import com.gavin.giframe.http.BaseResponse;
import com.suncn.ihold_zxztc.bean.*;
import com.suncn.ihold_zxztc.bean.chat.ChatRoomInfoData;
import com.suncn.ihold_zxztc.bean.chat.ContactSearchListBean;
import com.suncn.ihold_zxztc.bean.chat.CreateChatRoomInfoData;
import com.suncn.ihold_zxztc.bean.chat.HistoryMessageListData;
import com.suncn.ihold_zxztc.bean.chat.RecentMessageListData;
import com.suncn.ihold_zxztc.bean.JointMemSearchListBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 * 请求接口
 */
public interface IRequestApi {
    //登录
    @POST("AuthServlet")
    Observable<BaseResponse<LoginBean>> doLogin(@QueryMap Map<String, String> map);

    //退出登录
    @POST("LogoutServlet")
    Observable<BaseResponse<BaseGlobal>> doLoginOut(@QueryMap Map<String, String> map);

    //检查更新
    @POST("AutoUpdateServlet")
    Observable<BaseResponse<AutoUpdateBean>> checkUpdates(@QueryMap Map<String, String> map);

    //获取新闻栏目
    @POST("MyNewsColumnServlet")
    Observable<BaseResponse<NewsColumnListBean>> getNewsColumn(@QueryMap Map<String, String> map);

    //获取新闻栏目（贵阳）
    @Headers("urlname:url_gy")
    @POST("gyzxweb/ios/MyNewsColumnServlet")
    Observable<BaseResponse<NewsColumnListBean>> getNewsColumnGy(@QueryMap Map<String, String> map);

    //获取新闻列表
    @POST("NewsInfoListServlet")
    Observable<BaseResponse<NewsListBean>> getNewsList(@QueryMap Map<String, String> map);

    //获取新闻列表(贵阳)
    @Headers("urlname:url_gy")
    @POST("gyzxweb/ios/NewsInfoListServlet")
    Observable<BaseResponse<NewsListBean>> getNewsListGy(@QueryMap Map<String, String> map);

    //独立单位列表（三级联动）
    @POST("UnitListServlet")
    Observable<BaseResponse<UnitListBean>> getUnitList(@QueryMap Map<String, String> map);

    //订阅取消订阅（独立机构）
    @POST("SubscribeNewsUnitServlet")
    Observable<BaseResponse<BaseTypeBean>> SubscribeNewsUnit(@QueryMap Map<String, String> map);


    //全部频道
    @POST("NewsColumnListServlet")
    Observable<BaseResponse<NewsColumnListBean>> getColumnSettingList(@QueryMap Map<String, String> map);

    //频道进行操作排序、添加、移除
    @POST("NCRecordByTypeServlet")
    Observable<BaseResponse<BaseGlobal>> getColumnSettingDeal(@QueryMap Map<String, String> map);

    //获取新闻回复列表
    @POST("NewsReplyListServlet")
    Observable<BaseResponse<ReplyListBean>> getNewsReplyList(@QueryMap Map<String, String> map);

    //新闻详情操作：回复、点赞、收藏
    @POST("AddNewsReplyOrRecordServlet")
    Observable<BaseResponse<ReplyBean>> getNewsDeal(@QueryMap Map<String, String> map);

    //获取全局搜索栏目
    @POST("SearchTypeServlet")
    Observable<BaseResponse<NewsColumnListBean>> getSearchColumn(@QueryMap Map<String, String> map);

    //消息提醒搜索栏目
    @POST("MsgTypeServlet")
    Observable<BaseResponse<NewsColumnListBean>> getRemindSearchColumn(@QueryMap Map<String, String> map);

    //通知公告搜索栏目
    @POST("NoticeTypeServlet")
    Observable<BaseResponse<NewsColumnListBean>> getNoticeSearchColumn(@QueryMap Map<String, String> map);

    //获取短信验证码
    @POST("RetrievePwdServlet")
    Observable<BaseResponse<SmsReceiveBean>> getSmsCode(@QueryMap Map<String, String> map);

    //验证短信验证码
    @POST("CheckCodeServlet")
    Observable<BaseResponse<BaseGlobal>> checkSmsCode(@QueryMap Map<String, String> map);

    //获取用户信息
    @POST("UserServlet")
    Observable<BaseResponse<UserBean>> getInfo(@QueryMap Map<String, String> map);

    //获取app活跃度
    @POST("ListAppServlet")
    Observable<BarChartsBean> getActiveInfo(@QueryMap Map<String, String> map);

    //修改用户信息
    @POST("ModiUserServlet")
    Observable<BaseResponse<BaseGlobal>> modifyUsePwd(@QueryMap Map<String, String> map);

    //意见反馈
    @POST("FeedBackServlet")
    Observable<BaseResponse<BaseGlobal>> feedBack(@QueryMap Map<String, String> map);

    //获取关于页面的url
    @POST("UrlConstantsServlet")
    Observable<BaseResponse<UrlConstantsBean>> getUrl(@QueryMap Map<String, String> map);

    //更新用户信息
    @POST("PersonModifyServlet")
    Observable<BaseResponse<BaseGlobal>> modifyInfo(@QueryMap Map<String, String> map);

    //获取发现页面的列表信息
    @POST("FindListServlet")
    Observable<FindListBean> getFindList(@QueryMap Map<String, String> map);

    //获取喜讯栏目
    @POST("FindColumnListServlet")
    Observable<NewsColumnListBean> getColumnList(@QueryMap Map<String, String> map);

    //获取圈子头部推荐关注列表
    @POST("RecommendNoticeListServlet")
    Observable<BaseResponse<CircleListBean>> getRecommendList(@QueryMap Map<String, String> map);

    //获取圈子底部动态列表
    @POST("DynamicListServlet")
    Observable<BaseResponse<CircleListBean>> getDynamicList(@QueryMap Map<String, String> map);

    //关注/取消关注
    @POST("AddDynamicNoticeServlet")
    Observable<BaseResponse<BaseGlobal>> addDynamicNotice(@QueryMap Map<String, String> map);

    //点赞
    @POST("AddDynamicRecordServlet")
    Observable<BaseResponse<BaseGlobal>> addZan(@QueryMap Map<String, String> map);

    //发表动态有图片
    @Multipart
    @POST("AddDynamicServlet")
    Observable<BaseResponse<BaseGlobal>> addDynamic(@Part() List<MultipartBody.Part> parts, @QueryMap Map<String, String> map);

    //编辑动态有图片
    @Multipart
    @POST("updateDynamicServlet")
    Observable<BaseResponse<BaseGlobal>> updateDynamicServlet(@Part() List<MultipartBody.Part> parts, @QueryMap Map<String, String> map);

    //删除
    @POST("DeleteReplyServlet")
    Observable<BaseResponse<BaseGlobal>> DeleteReplyServlet(@QueryMap Map<String, String> map);

    //个人主页
    @POST("MemberHomeServlet")
    Observable<BaseResponse<PersonPageBean>> getPersonPage(@QueryMap Map<String, String> map);

    //添加评论
    @POST("AddDynamicReply")
    Observable<BaseResponse<ReplyBean>> addReply(@QueryMap Map<String, String> map);

    //获取评论列表
    @POST("DynamicReplyListServlet")
    Observable<BaseResponse<CommentDetailBean>> getCommentList(@QueryMap Map<String, String> map);

    //我的关注
    @POST("DynamicNoticeListServlet")
    Observable<BaseResponse<CircleListBean>> getNoticeList(@QueryMap Map<String, String> map);

    //图灵机器人
    @POST("openapi/api/v2")
    Call<TuLingResultBean> sendTuLingMsg(@Body TuLingRequestBean requestBean);

    //获取履职情况
    @POST("MemRecordServlet")
    Observable<BaseResponse<PerformDutyBean>> getPerformDutyMassage(@QueryMap Map<String, String> map);

    //获取履职排行列表
    @POST("MemRecordRankListServlet")
    Observable<BaseResponse<MemRecordRankListBean>> getMemRecordRankList(@QueryMap Map<String, String> map);

    //委员履职得分情况
    @POST("MemRecordScoreServlet")
    Observable<BaseResponse<MemRecordScoreBean>> getMemRecordScore(@QueryMap Map<String, String> map);

    //委员履职反馈
    @POST("MemberRecordBackServlet")
    Observable<BaseResponse<BaseGlobal>> addMemberRecord(@QueryMap Map<String, String> map);

    //新闻播报的信息
    @POST("BroadcastListServlet")
    Observable<BaseResponse<BroadcastListBean>> getPlayNews(@QueryMap Map<String, String> map);

    //新闻资讯收藏列表
    @POST("CollectListServlet")
    Observable<BaseResponse<CollectionListBean>> getCollectionList(@QueryMap Map<String, String> map);

    //应用菜单列表
    @POST("MyApplicationListServlet")
    Observable<BaseResponse<ApplicationListBean>> getApplicationList(@QueryMap Map<String, String> map); //新闻资讯收藏列表

    //全局搜索列表
    @POST("SearchListServlet")
    Observable<BaseResponse<OverSearchBean>> getOverSearchList(@QueryMap Map<String, String> map);

    //消息提醒列表
    @POST("MsgListServlet")
    Observable<BaseResponse<MessageRemindListBean>> getMessageRemindSearchList(@QueryMap Map<String, String> map);

    //提案列表  Old
    @POST("MyMotionListByTypeServlet")
    Observable<BaseResponse<ProposalListBean>> getProposalList(@QueryMap Map<String, String> map);

    //提案列表  New(V5.0)
    @POST("MyProposalListByTypeServlet")
    Observable<BaseResponse<ProposalListBean>> getMyProposalListByTypeServlet(@QueryMap Map<String, String> map);

    //提案提交
    @POST("MotionAddServlet")
    Observable<BaseResponse<BaseGlobal>> sumbitProposal(@QueryMap Map<String, String> map);

    //相似提案提列表
    @POST("MotionAddServlet")
    Observable<BaseResponse<SimilarProposalListBean>> getSimilarProposal(@QueryMap Map<String, String> map);

    //相似提案提列表  New (V5.0)
    @POST("ProposalAddServlet")
    Observable<BaseResponse<SimilarProposalListBean>> getProposalAddServlet(@QueryMap Map<String, String> map);

    //提案提交协商方式
    @POST("MotionHandlerWayServlet")
    Observable<BaseResponse<MotionHandlerWayBean>> getHandlerWayOptions(@QueryMap Map<String, String> map);

    //提案提交协商方式 New(V5.0)
    @POST("ProposalHandleWayServlet")
    Observable<BaseResponse<MotionHandlerWayBean>> getProposalHandleWayServlet(@QueryMap Map<String, String> map);

    //提案提交其他选项
    @POST("MotionOtherOptionServlet")
    Observable<BaseResponse<OtherOptionBean>> getOtherOptions(@QueryMap Map<String, String> map);

    //提案提交其他选项  New (V5.0)
    @POST("ProposalConfigServlet")
    Observable<BaseResponse<OtherOptionBean>> getProposalConfigServlet(@QueryMap Map<String, String> map);

    //获取草稿箱再次编辑的信息
    @POST("MotionDraftServlet")
    Observable<BaseResponse<SumbitInfoBean>> getProposalInfo(@QueryMap Map<String, String> map);

    //获取草稿箱再次编辑的信息  New(V5.0)
    @POST("ProposalAddViewServlet")
    Observable<BaseResponse<SumbitInfoBean>> getProposalAddViewServlet(@QueryMap Map<String, String> map);

    //删除草稿箱提案
    @POST("MotionDeleteServlet")
    Observable<BaseResponse<BaseGlobal>> deleteProposal(@QueryMap Map<String, String> map);

    //删除草稿箱提案   New(V5.0)
    @POST("ProposalDeleteServlet")
    Observable<BaseResponse<BaseGlobal>> deleteProposalDeleteServlet(@QueryMap Map<String, String> map);

    //修改已读状态
    @POST("ReadServlet")
    Observable<BaseResponse<ProposalViewBean>> changeReadState(@QueryMap Map<String, String> map);

    //提案详情
    @POST("MotionViewServlet")
    Observable<BaseResponse<ProposalViewBean>> getProposalDetail(@QueryMap Map<String, String> map);

    //提案详情  New (V5.0)
    @POST("ProposalViewServlet")
    Observable<BaseResponse<ProposalViewBean>> getProposalViewServlet(@QueryMap Map<String, String> map);

    //提案签收
    @POST("MotionDistSignServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalSign(@QueryMap Map<String, String> map);

    //公开提案查询列表
    @POST("AllMotionListServlet")
    Observable<BaseResponse<ProposalListBean>> getPublicProposalList(@QueryMap Map<String, String> map);

    //提案类别列表
    @POST("CaseTopicListServlet")
    Observable<BaseResponse<ProposalTypeListBean>> getProposalTypeList(@QueryMap Map<String, String> map);

    //提案分类Tab  Old
    @POST("MotionTabServlet")
    Observable<BaseResponse<ProposalTabListBean>> getProposalTabList(@QueryMap Map<String, String> map);

    //提案分类Tab  New(V5.0)
    @POST("ProposalTabServlet")
    Observable<BaseResponse<ProposalTabListBean>> getProposalTabServlet(@QueryMap Map<String, String> map);

    //社情民意列表
    @POST("AllInfoListServlet")
    Observable<BaseResponse<PublicOpinionListBean>> getPubublicOpinionList(@QueryMap Map<String, String> map);

    //社情民意提交
    @POST("InfoAddServlet")
    Observable<BaseResponse<SimilarProposalListBean>> sumbitPublicOpinionInfo(@QueryMap Map<String, String> map);

    //社情民意暂存
    @POST("InfoAddViewServlet")
    Observable<BaseResponse<PublicOpinionListBean.ObjListBean>> InfoAddViewServlet(@QueryMap Map<String, String> map);

    //更多刊物列表
    @POST("PublicationListServlet")
    Observable<BaseResponse<MorePublicOpinionsListBean>> getMorePubublicOpinionList(@QueryMap Map<String, String> map);

    //会议列表委员
    @POST("MeetMemListServlet")
    Observable<BaseResponse<MeetingActListBean>> getMeetingList(@QueryMap Map<String, String> map);

    //会议列表机关
    @POST("MeetListServlet")
    Observable<BaseResponse<MeetingActListBean>> getMangerMeetingList(@QueryMap Map<String, String> map);

    //会议出席情况
    @POST("MeetAttendListServlet")
    Observable<BaseResponse<AttendPersonListBean>> getMeetAttendList(@QueryMap Map<String, String> map);

    //机关端会议次会列表
    @POST("MeetChildListServlet")
    Observable<BaseResponse<MeetingActListBean>> getMangerChildMeetingList(@QueryMap Map<String, String> map);

    //会议次会列表
    @POST("MeetingChildListServlet")
    Observable<BaseResponse<MeetingActListBean>> getChildMeetingList(@QueryMap Map<String, String> map);

    //活动列表委员
    @POST("EventMemListServlet")
    Observable<BaseResponse<MeetingActListBean>> getActList(@QueryMap Map<String, String> map);

    //活动管理列表
    @POST("EventListServlet")
    Observable<BaseResponse<MeetingActListBean>> getMangerActList(@QueryMap Map<String, String> map);

    //活动出席情况
    @POST("EventAttendListServlet")
    Observable<BaseResponse<AttendPersonListBean>> getActAttendList(@QueryMap Map<String, String> map);

    //请假类型列表
    @POST("AbsentTypeListServlet")
    Observable<BaseResponse<LeaveTypeListBean>> getLeaveTypeList(@QueryMap Map<String, String> map);

    //会议、活动报名、修改状态带附件
    @Multipart
    @POST("UpdateAttendServlet")
    Observable<BaseResponse<BaseGlobal>> dealWith(@Part() List<MultipartBody.Part> parts, @QueryMap Map<String, String> map);


    //管理端修改活动参与状态
    @POST("UpdateEventAttendServlet")
    Observable<BaseResponse<BaseGlobal>> UpdateEventAttendServlet(@QueryMap Map<String, String> map);

    //管理端修改会议参与状态
    @POST("UpdateMeetAttendServlet")
    Observable<BaseResponse<BaseGlobal>> UpdateMeetAttendServlet(@QueryMap Map<String, String> map);


    //会议出席状态
    @POST("MeetResultStateServlet")
    Observable<BaseResponse<AttendStateListBean>> meetingState(@QueryMap Map<String, String> map);

    //会议请假信息
    @POST("MeetApplyViewServlet")
    Observable<BaseResponse<AuditLeaveBean>> getMeetLeaveInfo(@QueryMap Map<String, String> map);

    //会议请假列表
    @POST("MeetApplyListServlet")
    Observable<BaseResponse<CheckLeaveListBean>> getMeetLeaveList(@QueryMap Map<String, String> map);

    //会议请假审核
    @POST("MeetApplyUpdateServlet")
    Observable<BaseResponse<BaseGlobal>> getMeetLeaveCheck(@QueryMap Map<String, String> map);

    //活动出席状态
    @POST("EventResultStateServlet")
    Observable<BaseResponse<AttendStateListBean>> actState(@QueryMap Map<String, String> map);

    //会议请假列表
    @POST("EventApplyListServlet")
    Observable<BaseResponse<CheckLeaveListBean>> getActLeaveList(@QueryMap Map<String, String> map);

    //活动请假信息
    @POST("EventApplyViewServlet")
    Observable<BaseResponse<AuditLeaveBean>> getActLeaveInfo(@QueryMap Map<String, String> map);

    //活动请假审核
    @POST("EventApplyUpdateServlet")
    Observable<BaseResponse<BaseGlobal>> getActLeaveCheck(@QueryMap Map<String, String> map);

    //会议详情
    @POST("MeetMemViewServlet")
    Observable<BaseResponse<MeetingViewBean>> getMeetView(@QueryMap Map<String, String> map);

    //会议详情机关
    @POST("MeetViewInfoServlet")
    Observable<BaseResponse<MeetingViewBean>> MeetViewInfoServlet(@QueryMap Map<String, String> map);


    //活动详情机关
    @POST("EventViewInfoServlet")
    Observable<BaseResponse<ActivityViewBean>> getMangerMeetView(@QueryMap Map<String, String> map);

    //活动详情委员
    @POST("EventMemViewServlet")
    Observable<BaseResponse<ActivityViewBean>> getActView(@QueryMap Map<String, String> map);

    //活动成果列表
    @POST("EventResultListServlet")
    Observable<BaseResponse<CollectionListBean>> getActResultList(@QueryMap Map<String, String> map);

    //社情民意提交
    @POST("InfoAddServlet")
    Observable<BaseResponse<BaseGlobal>> sumbitInfo(@QueryMap Map<String, String> map);

    //网络议政列表
    @POST("SubjectListServlet")
    Observable<BaseResponse<ThemeListBean>> getThemeList(@QueryMap Map<String, String> map);

    //网络议政回复列表
    @POST("ReplyListServlet")
    Observable<BaseResponse<ReplyListBean>> getReplyList(@QueryMap Map<String, String> map);

    //网络议政操作
    @POST("AddSubjectReplyOrRecordServlet")
    Observable<BaseResponse<ReplyBean>> dealThemeInfo(@QueryMap Map<String, String> map);

    //会议活动签到
    @POST("LocationCheckServlet")
    Observable<BaseResponse<IosAttendBean>> dealSign(@QueryMap Map<String, String> map);

    //会议发言列表
    @POST("MeetSpeakMemListServlet")
    Observable<BaseResponse<MeetSpeakListBean>> getMeetSpeakList(@QueryMap Map<String, String> map);

    //会议发言列表
    @POST("SpeakNoticeMemListServlet")
    Observable<BaseResponse<MeetSpeakNoticeListBean>> getMeetSpeakNoticeList(@QueryMap Map<String, String> map);

    //提交会议发言
    @POST("MeetSpeakAddServlet")
    Observable<BaseResponse<BaseGlobal>> submitMeetSpeakInfo(@QueryMap Map<String, String> map);

    //届次信息列表
    @POST("ProposalSessionListServlet")
    Observable<BaseResponse<SessionListBean>> getSessionList(@QueryMap Map<String, String> map);

    //联名人、附议人列表
    @POST("JoinOrSupportListByMotioIdServlet")
    Observable<BaseResponse<JoinSupportListBean>> getJoinSupportList(@QueryMap Map<String, String> map);

    //联名人、附议人列表 New(V5.0)
    @POST("AllyListByIdServlet")
    Observable<BaseResponse<JoinSupportListBean>> getAllyListByIdServlet(@QueryMap Map<String, String> map);

    //联名人、附议人确认
    @POST("JoinOrSupportSureServlet")
    Observable<BaseResponse<BaseGlobal>> dealJoinOrSupport(@QueryMap Map<String, String> map);

    //联名人、附议人确认  New(V5.0)
    @POST("AllySureServlet")
    Observable<BaseResponse<BaseGlobal>> dealAllySureServlet(@QueryMap Map<String, String> map);

    //社情民意联名
    @POST("InfoAllySureServlet")
    Observable<BaseResponse<BaseGlobal>> InfoAllySureServlet(@QueryMap Map<String, String> map);

    //附议人确认（详情右上角）
    @POST("SupportServlet")
    Observable<BaseResponse<BaseGlobal>> dealSupport(@QueryMap Map<String, String> map);

    //联名人
    @POST("ChooseReportUserServlet")
    Observable<BaseResponse<ZxtaJointMemListBean>> getJoinMemList(@QueryMap Map<String, String> map);

    //联名人  New(V5.0)
    @POST("ChooseReportUserServlet")
    Observable<BaseResponse<ZxtaJointMemListBean>> getChooseReportUserServlet(@QueryMap Map<String, String> map);

    //联名人搜索
    @POST("ChooseReportUserServlet")
    Observable<BaseResponse<JointMemSearchListBean>> getJoinMemSearchList(@QueryMap Map<String, String> map);

    //联名人
    @POST("UpUnitServlet")
    Observable<BaseResponse<UpUnitListBean>> getSuggestUnitList(@QueryMap Map<String, String> map);

    //联名人  New(V5.0)
    @POST("ChooseReportUnitServlet")
    Observable<BaseResponse<UpUnitListBean>> getChooseReportUnitServlet(@QueryMap Map<String, String> map);

    //群信息
    @POST("OpenFireMucMemberByRoomID")
    Observable<BaseResponse<ChatRoomInfoData>> getGroupInfo(@QueryMap Map<String, String> map);

    //群操作
    @POST("OpenFireMucRoomByTypeServlet")
    Observable<BaseResponse<CreateChatRoomInfoData>> dealGroup(@QueryMap Map<String, String> map);

    //获取历史消息
    @POST("OpenFireMsgByLinkListServlet")
    Observable<BaseResponse<HistoryMessageListData>> getHistoryList(@QueryMap Map<String, String> map);

    //更新已读消息
    @POST("OpenFireReadMsgServlet")
    Observable<BaseResponse<BaseGlobal>> updateReadInfo(@QueryMap Map<String, String> map);

    //创建群
    @POST("OpenFireMucRoomCreateServlet")
    Observable<BaseResponse<CreateChatRoomInfoData>> creatGroup(@QueryMap Map<String, String> map);

    //界别党派查询委员
    @POST("MemberSessionListServlet")
    Observable<BaseResponse<JointMemSearchListBean>> getMemList(@QueryMap Map<String, String> map);

    //界别党派查询委员
    @POST("MemberSessionListServlet")
    Observable<BaseResponse<ZxtaJointMemListBean>> getMemDutyList(@QueryMap Map<String, String> map);

    //委员查询
    @POST("SearchPersonUnitServlet")
    Observable<BaseResponse<JointMemSearchListBean>> getMemSearchList(@QueryMap Map<String, String> map);

    //创建群  通讯录列表
    @POST("ChatUserOrMemberListServlet")
    Observable<BaseResponse<ZxtaJointMemListBean>> getContactList(@QueryMap Map<String, String> map);

    //群组列表
    @POST("OpenFireMucRoomListServlet")
    Observable<BaseResponse<RecentMessageListData>> getGroupList(@QueryMap Map<String, String> map);

    //通讯录查询列表
    @POST("ChatUserOrMemberListServlet")
    Observable<BaseResponse<JointMemSearchListBean>> getContactSearchList(@QueryMap Map<String, String> map);

    //通讯录跳转查询列表
    @POST("ChatUserOrMemberListServlet")
    Observable<BaseResponse<ContactSearchListBean>> getContactTabSearchList(@QueryMap Map<String, String> map);

    //通讯录跳转查询列表
    @POST("OpenFireMucRoomListServlet")
    Observable<BaseResponse<ContactSearchListBean>> getGroupTabSearchList(@QueryMap Map<String, String> map);

    //通讯录查询列表
    @POST("OpenFireAllMsgListServlet")
    Observable<BaseResponse<RecentMessageListData>> geRecentChatList(@QueryMap Map<String, String> map);

    //消息提醒列表
    @POST("MsgListServlet")
    Observable<BaseResponse<MessageRemindListBean>> geMessageRemindList(@QueryMap Map<String, String> map);

    //消息已读
    @POST("ReadServlet")
    Observable<BaseResponse<BaseGlobal>> dealReadRemind(@QueryMap Map<String, String> map);

    //通知公告列表
    @POST("NoticeListServlet")
    Observable<BaseResponse<NoticeAnnouncementListBean>> getNoticeAnnouncementList(@QueryMap Map<String, String> map);

    //通知公告搜索列表
    @POST("NoticeListServlet")
    Observable<BaseResponse<NoticeAnnouncementSearchListBean>> getNoticeAnnouncementSearchList(@QueryMap Map<String, String> map);

    //视频协商列表
    @POST("VideoMeetAttendListServlet")
    Observable<BaseResponse<VideoMtListBean>> getChatMeetingList(@QueryMap Map<String, String> map);

    //政协提案委员反馈
    @POST("ZxtaMemReturnServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalFeedBack(@QueryMap Map<String, String> map);

    //政协提案委员反馈  New(V5.0)
    @POST("ProposalFeedBackServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalFeedBackServlet(@QueryMap Map<String, String> map);

    //委员反馈的信息   New(新增加 V5.0)
    @POST("ProposalFeedBackViewServlet")
    Observable<BaseResponse<ProposalFeedBackViewServletBean>> getProposalFeedBackViewServlet(@QueryMap Map<String, String> map);

    //办理追踪
    @POST("MotionDistListServlet")
    Observable<BaseResponse<ProposalTrackListBean>> getProposaltracklist(@QueryMap Map<String, String> map);

    //提案办理
    @POST("MotionListByTypeServlet")
    Observable<BaseResponse<ProposalTrackListBean>> getProposaldeallist(@QueryMap Map<String, String> map);

    //办理追踪
    @POST("MotionCheckListServlet")
    Observable<BaseResponse<ProposalTrackListBean>> getProposalAuditlist(@QueryMap Map<String, String> map);

    //获取审核信息
    @POST("MotionCheckGetServlet")
    Observable<BaseResponse<AuditInfoListBean>> getProposalAuditInfo(@QueryMap Map<String, String> map);

    //获取提案去向
    @POST("MotionCheckResultTypeServlet")
    Observable<BaseResponse<ProposalGoTypeListBean>> getProposalGotoList(@QueryMap Map<String, String> map);

    //获取不予立案理由
    @POST("NoLianReasonList  ")
    Observable<BaseResponse<ProposalGoTypeListBean>> NoLianReasonList(@QueryMap Map<String, String> map);

    //获取提案分类
    @POST("CaseTopicAllListServlet")
    Observable<BaseResponse<ProposalClassificationListBean>> getClassificationList(@QueryMap Map<String, String> map);

    //提案审核
    @POST("MotionCheckServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalAudit(@QueryMap Map<String, String> map);

    //会议发言审核操作
    @POST("MeetSpeakCheckServlet")
    Observable<BaseResponse<BaseGlobal>> checkMeetSpeak(@QueryMap Map<String, String> map);

    //会议发言列表机关
    @POST("MeetSpeakCheckListServlet")
    Observable<BaseResponse<MeetingSpeakListBean>> getMangerMeetSpeakList(@QueryMap Map<String, String> map);

    //社情民意列表机关
    @POST("InfoCheckListServlet")
    Observable<BaseResponse<MeetingSpeakListBean>> getMangerOpinionsList(@QueryMap Map<String, String> map);

    //社情民意列表委员
    @POST("MyInfoListByTypeServlet")
    Observable<BaseResponse<MeetingSpeakListBean>> MyInfoListByTypeServlet(@QueryMap Map<String, String> map);

    //社情民意机关tab
    @POST("InfoTypeServlet")
    Observable<BaseResponse<TabBean>> InfoTypeServlet(@QueryMap Map<String, String> map);

    //社情民意机关列表
    @POST("InfoListByTypeServlet")
    Observable<BaseResponse<MeetingSpeakListBean>> InfoListByTypeServlet(@QueryMap Map<String, String> map);

    //社情民意tab
    @POST("InfoTabServlet")
    Observable<BaseResponse<TabBean>> InfoTabServlet(@QueryMap Map<String, String> map);

    //社情民意主题类型
    @POST("InfoTypeAllListServlet")
    Observable<BaseResponse<ThemeTypeBean>> InfoTypeAllListServlet(@QueryMap Map<String, String> map);

    //社情民意审核操作
    @POST("InfoCheckServlet")
    Observable<BaseResponse<BaseGlobal>> checkOpinions(@QueryMap Map<String, String> map);

    //获取解决程度
    @POST("ParamByTypeServlet")
    Observable<BaseResponse<ParamByTypeBean>> getParamByType(@QueryMap Map<String, String> map);

    //社情民意办理回复
    @POST("PubDistHanlerServlet")
    Observable<BaseResponse<BaseGlobal>> dealOinionHander(@QueryMap Map<String, String> map);

    //会提案办理回复带附件
    @Multipart
    @POST("MotionDistHanlerServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalHanderWithFile(@Part() List<MultipartBody.Part> parts, @QueryMap Map<String, String> map);

    //发送提醒
    @POST("MotionRemindSignServlet")
    Observable<BaseResponse<BaseGlobal>> dealSendRemind(@QueryMap Map<String, String> map);

    //发送提醒
    @POST("MotionUrgeServlet")
    Observable<BaseResponse<BaseGlobal>> sednUrge(@QueryMap Map<String, String> map);

    //催委员反馈
    @POST("MotionUrgeMemberBackServlet")
    Observable<BaseResponse<BaseGlobal>> sendUrgeFeedBack(@QueryMap Map<String, String> map);

    //催委员反馈
    @POST("MotionAfreshHandlerServlet")
    Observable<BaseResponse<BaseGlobal>> sendTwoHandler(@QueryMap Map<String, String> map);

    //获取社情民意延期审核信息
    @POST("PubDelayCheckGetServlet")
    Observable<BaseResponse<DelayAuditBean>> getOpinionDelayInfo(@QueryMap Map<String, String> map);

    //获取提案延期审核信息
    @POST("MotionDelayCheckGetServlet")
    Observable<BaseResponse<DelayAuditBean>> getProposalDelayInfo(@QueryMap Map<String, String> map);

    //社情民意延期审核操作
    @POST("PubDelayCheckServlet")
    Observable<BaseResponse<BaseGlobal>> dealOpinionDelayAudit(@QueryMap Map<String, String> map);

    //提案延期审核操作
    @POST("MotionDelayCheckServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalDelayAudit(@QueryMap Map<String, String> map);

    //提案延期审核操作
    @POST("MotionListByTypeServlet")
    Observable<BaseResponse<ProposalTrackListBean>> getProposalHandlerList(@QueryMap Map<String, String> map);

    //提案分发
    @POST("MotionDistFenfaServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalFenfa(@QueryMap Map<String, String> map);

    //提案分发
    @POST("PubDistDelayServlet")
    Observable<BaseResponse<BaseGlobal>> dealOpinionDelay(@QueryMap Map<String, String> map);

    //提案分发
    @POST("MotionDistDelayServlet")
    Observable<BaseResponse<BaseGlobal>> dealProposalDelay(@QueryMap Map<String, String> map);

    //签到扫码确认
    @POST("QRCodeSignServlet")
    Observable<BaseResponse<BaseGlobal>> dealQRSignConfirm(@QueryMap Map<String, String> map);

    //获取扫码信息
    @POST("QRCodeSignViewServlet")
    Observable<BaseResponse<SignConfirmBean>> getQRSignInfo(@QueryMap Map<String, String> map);

    //会议扫码签到
    @POST("QRCheckServlet")
    Observable<BaseResponse<SignConfirmBean>> QRCheckServlet(@QueryMap Map<String, String> map);

    //获取全会指南模块
    @POST("MeetingGuideServlet")
    Observable<BaseResponse<NewsColumnListBean>> getPlenaryMeeting(@QueryMap Map<String, String> map);

    //获取会议日程
    @POST("MeetingAgendaServlet")
    Observable<BaseResponse<HYRCListBean>> MeetingAgendaServlet(@QueryMap Map<String, String> map);

    //获取会议日程时间
    @POST("MeetingScheduleServlet")
    Observable<BaseResponse<NewsColumnListBean>> MeetingScheduleServlet(@QueryMap Map<String, String> map);

    //获取住宿安排
    @POST("RoomArrangeServlet")
    Observable<BaseResponse<RoomArrangeBean>> RoomArrangeServlet(@QueryMap Map<String, String> map);

    //获取菜单
    @POST("MenuManagerServlet")
    Observable<BaseResponse<MenuListBean>> MenuManagerServlet(@QueryMap Map<String, String> map);

    //获取乘车安排
    @POST("CarArrangementServlet")
    Observable<BaseResponse<RideArrangeListBean>> CarArrangementServlet(@QueryMap Map<String, String> map);

    //获取h5页面的地址
    @POST("ToViewServlet")
    Observable<BaseResponse<UrlConstantsBean>> ToViewServlet(@QueryMap Map<String, String> map);

    //新增视频会议
    @POST("VideoMeetAttendAddServlet")
    Observable<BaseResponse<BaseGlobal>> VideoMeetAttendAddServlet(@QueryMap Map<String, String> map);

    //获取角色列表
    @POST("ChangeRootUnitByIdServlet")
    Observable<BaseResponse<MulUnitBean>> ChangeRootUnitById(@QueryMap Map<String, String> map);

    //获取标签列表
    @POST("GetLabelServlet")
    Observable<BaseResponse<CircleTagListBean>> GetLabelServlet(@QueryMap Map<String, String> map);

    //保存标签
    @POST("SetLabelServlet")
    Observable<BaseResponse<BaseGlobal>> SetLabelServlet(@QueryMap Map<String, String> map);

    //获取已关注标签列表
    @POST("GetMemberFollowServlet")
    Observable<BaseResponse<CircleTagListBean>> GetMemberFollowServlet(@QueryMap Map<String, String> map);

    //获取提案分发
    @POST("MotionDistTypeServlet")
    Observable<BaseResponse<MotionDistTypeServletBean>> getMotionDistTypeServlet(@QueryMap Map<String, String> map);

    //删除动态
    @POST("DeleteDynamicServlet")
    Observable<BaseResponse<BaseGlobal>> deleteDynamic(@QueryMap Map<String, String> map);

    //获取动态通知数据
    @POST("DynamicNoticeServlet")
    Observable<BaseResponse<MsgRemindListBean>> DynamicNoticeServlet(@QueryMap Map<String, String> map);

    //获取动态通知未读数量
    @POST("GetCircleNotRedNumber")
    Observable<BaseResponse<BaseTypeBean>> GetCircleNotRedNumber(@QueryMap Map<String, String> map);

    //安安问答
    @POST("KnowledgeQuestionAServlet")
    Observable<BaseResponse<BroadcastListBean>> KnowledgeQuestionAServlet(@QueryMap Map<String, String> map);

    //委员学习Tab
    @POST("LearnRegulationColumnServlet")
    Observable<BaseResponse<ProposalTabListBean>> LearnRegulationColumnServlet(@QueryMap Map<String, String> map);

    //委员学习列表
    @POST("LearnMaterialListServlet")
    Observable<BaseResponse<StudyBean>> LearnMaterialListServlet(@QueryMap Map<String, String> map);

    //积分添加
    @POST("CommonReadRuleServlet")
    Observable<BaseResponse<BaseGlobal>> CommonReadRuleServlet(@QueryMap Map<String, String> map);

    //获取积分
    @POST("GetALlIntegralServlet")
    Observable<BaseResponse<IosAttendBean>> GetALlIntegralServlet(@QueryMap Map<String, String> map);

    //获取生日祝福参数
    @POST("GetBirthDayDataServlet")
    Observable<BaseResponse<ActivityBean>> GetBirthDayDataServlet(@QueryMap Map<String, String> map);

    //隐藏生日祝福弹窗
    @POST("HideBirthPopupWithLoginUserServlet")
    Observable<BaseResponse<BaseGlobal>> HideBirthPopupWithLoginUserServlet(@QueryMap Map<String, String> map);

    //获取是否有运营活动
    @POST("IsHavingEventServlet")
    Observable<BaseResponse<ActivityBean>> IsHavingEventServlet(@QueryMap Map<String, String> map);

    //接受活动邀请
    @POST("AcceptEventServlet")
    Observable<BaseResponse<BaseGlobal>> AcceptEventServlet(@QueryMap Map<String, String> map);

    //接受会议邀请
    @POST("AcceptMeetServlet")
    Observable<BaseResponse<BaseGlobal>> AcceptMeetServlet(@QueryMap Map<String, String> map);

    //活动请假
    @Multipart
    @POST("EventMemApplyServlet")
    Observable<BaseResponse<BaseGlobal>> EventMemApplyServlet(@Part() List<MultipartBody.Part> parts, @QueryMap Map<String, String> map);

    //会议请假
    @Multipart
    @POST("MeetMemApplyServlet")
    Observable<BaseResponse<BaseGlobal>> MeetMemApplyServlet(@Part() List<MultipartBody.Part> parts, @QueryMap Map<String, String> map);

    //撤回请假活动
    @POST("RecallEventApplyServlet")
    Observable<BaseResponse<BaseGlobal>> RecallEventApplyServlet(@QueryMap Map<String, String> map);

    //撤回请假会议
    @POST("RecallMeetApplyServlet")
    Observable<BaseResponse<BaseGlobal>> RecallMeetApplyServlet(@QueryMap Map<String, String> map);

    //获取刊物类型
    @POST("PubTypeListServlet")
    Observable<BaseResponse<ParamByTypeBean>> PubTypeListServlet(@QueryMap Map<String, String> map);

    //获取反映人类型
    @POST("ReportUserTypeListServlet")
    Observable<BaseResponse<ParamByTypeBean>> ReportUserTypeListServlet(@QueryMap Map<String, String> map);

    //获取状态
    @POST("StateListByTypeServlet")
    Observable<BaseResponse<ParamByTypeBean>> StateListByTypeServlet(@QueryMap Map<String, String> map);

    //提案列表的类型  New(V5.0)增加
    @POST("ProposalAllTypeServlet")
    Observable<BaseResponse<ProposalAllTypeServletBean>> getProposalAllTypeServlet(@QueryMap Map<String, String> map);

    //机关端的管理列表  New(V5.0)增加
    @POST("ProposalAllListByTypeServlet")
    Observable<BaseResponse<ProposalAllListByTypeServletBean>> getProposalAllListByTypeServlet(@QueryMap Map<String, String> map);

    //会议发言回复
    @POST("MeetSpeakNoticeReplyServlet")
    Observable<BaseResponse<BaseGlobal>> MeetSpeakNoticeReplyServlet(@QueryMap Map<String, String> map);

    //机关端的管理列表  New(V5.0)增加
    @POST("ProposalTypeAllListServlet")
    Observable<BaseResponse<ProposalTypeAllListServletBean>> getProposalTypeAllListServlet(@QueryMap Map<String, String> map);

    //扫码登录确认
    @POST("loginByAppSweepCode")
    Observable<BaseResponse<BaseGlobal>> loginByAppSweepCode(@QueryMap Map<String, String> map);

    //机关端的管理列表  New(V5.0)增加
    @POST("ListAllYearServlet")
    Observable<BaseResponse<ListAllYearServletBean>> getListAllYearServlet(@QueryMap Map<String, String> map);

    //机关端的管理列表  New(V5.0)增加
    @POST("ProposalSessionListServlet")
    Observable<BaseResponse<SessionListBean>> getProposalSessionListServlet(@QueryMap Map<String, String> map);

    //刪除社情民意
    @POST("InfoDeleteServlet")
    Observable<BaseResponse<BaseGlobal>> InfoDeleteServlet(@QueryMap Map<String, String> map);

    //新增会议
    @POST("AddVideoMeetingServlet")
    Observable<BaseResponse<BaseGlobal>> AddStartMeetingServlet(@QueryMap Map<String, String> map);

    //新增会议
    @POST("MyVideoMeetingListServlet")
    Observable<BaseResponse<BaseMeetBean>> MyStartMeetingListServlet(@QueryMap Map<String, String> map);

    //会议详情
    @POST("VideoMeetingViewServlet")
    Observable<BaseResponse<BaseMeetDetailBean>> VideoMeetingViewServlet(@QueryMap Map<String, String> map);

    //提醒入会
    @POST("SendInviteMeetingMsg")
    Observable<BaseResponse<BaseGlobal>> SendInviteMeetingMsg(@QueryMap Map<String, String> map);

}
