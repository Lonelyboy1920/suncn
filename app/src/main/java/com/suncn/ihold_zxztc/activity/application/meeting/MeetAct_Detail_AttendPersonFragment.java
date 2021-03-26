package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.*;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.MeetAct_AttendPerson_Search_RVAdapter;
import com.suncn.ihold_zxztc.adapter.AttendPerson_ExLVAdapter;
import com.suncn.ihold_zxztc.bean.ActivityViewBean;
import com.suncn.ihold_zxztc.bean.AttendPersonListBean;
import com.suncn.ihold_zxztc.bean.MeetingViewBean;
import com.suncn.ihold_zxztc.bean.ObjAttendMemBean;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyLetterView;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 参会人员（出席情况）fragment
 */
public class MeetAct_Detail_AttendPersonFragment extends BaseFragment implements OptionSearch.IFinishListener {
    @BindView(id = R.id.recyclerView)
    private RecyclerView search_recyclerView;//搜索列表
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.ll_search_attend)
    private LinearLayout searchAttend_LinearLayout;//查询
    @BindView(id = R.id.ll_top_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_top_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel_1, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.tv_search, click = true)
    private TextView search_TextView;
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//界次LinearLayout
    @BindView(id = R.id.tv_empty)
    private ImageView empty_TextView;
    @BindView(id = R.id.exListview)
    private ExpandableListView expandableListView;
    @BindView(id = R.id.myLetterView, click = true)
    private MyLetterView myLetterView;
    @BindView(id = R.id.tv_float_group)
    private TextView floatGroup_TextView;
    private static List<ObjAttendMemBean> objPersonListBeen; // 参会人员（活动通知）
    private static int sign;
    private static String strId;
    private int strTypeCommissioner = 0; // 委员管理展示类别
    private static ActivityViewBean activityViewBean;
    private static MeetingViewBean meetingViewBean;
    private String strKeyValue = ""; // 搜索关键字
    private int curPage = 1;
    private OptionSearch mOptionSearch; // 搜索工具类
    private AttendPerson_ExLVAdapter exLVAdapter;
    private ArrayList<MeetingViewBean.ChildMtForWorker> childMtForWorkers;
    private String[] chiMtArray;
    private ArrayList<String> objList;
    private SpinerPopWindow<String> mSpinerPopWindow;
    private boolean isLetterClick = false;
    private MeetAct_AttendPerson_Search_RVAdapter searchAdapter;
    public String intAttend = "";
    public static String strAttendId;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    getActJoinMemInfo(false);
                    GILogUtil.i("这个时候能触发这里吗");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_meetact_attendperson, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static MeetAct_Detail_AttendPersonFragment newInstance(int mSign, String mStrId, String mStrAttendId, ActivityViewBean activityViewBeans, MeetingViewBean meetingViewBeans) {
        MeetAct_Detail_AttendPersonFragment memListFragment = new MeetAct_Detail_AttendPersonFragment();
        sign = mSign;
        strId = mStrId;
        strAttendId = mStrAttendId;
        activityViewBean = activityViewBeans;
        meetingViewBean = meetingViewBeans;
        return memListFragment;
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, search_recyclerView, true, false, R.color.main_bg, 1, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getActJoinMemInfo(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage = curPage + 1;
                getActJoinMemInfo(false);
            }
        });
        searchAdapter = new MeetAct_AttendPerson_Search_RVAdapter(activity);
        search_recyclerView.setAdapter(searchAdapter);
    }

    /**
     * item点击事件
     */
    private void doItemClick(ObjAttendMemBean joinMemSearchBean) {
        if (exLVAdapter.isIntModify()) {
            final int intAttend = joinMemSearchBean.getIntAttend();
            ArrayList<ObjFileBean> objFileBeans;//我的活动附件
            objFileBeans = new ArrayList<>();
            if (joinMemSearchBean.getAffix() != null && joinMemSearchBean.getAffix().size() > 0) {
                for (int i = 0; i < joinMemSearchBean.getAffix().size(); i++) {
                    ObjFileBean objFileMeetBean = joinMemSearchBean.getAffix().get(i);
                    objFileMeetBean.setStrFile_url(Utils.formatFileUrl(activity, joinMemSearchBean.getAffix().get(i).getStrFile_url()));
                    objFileMeetBean.setStrFile_Type(GIMyIntent.getMIMEType(new File(joinMemSearchBean.getAffix().get(i).getStrFile_url())));
                    objFileBeans.add(objFileMeetBean);
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString("strAttendId", joinMemSearchBean.getStrAttendId());
            bundle.putString("headTitle", getString(R.string.string_modify_participation_status));
            bundle.putInt("sign", sign);
            bundle.putString("leaveTypeId", joinMemSearchBean.getStrAbsentType());//请假类型
            bundle.putString("strReason", joinMemSearchBean.getStrReason());//请假原因
            bundle.putBoolean("isWorker", true);
            bundle.putString("strLeaveType", joinMemSearchBean.getStrLeaveType());
            bundle.putSerializable("FileList", objFileBeans);
            if (activityViewBean != null) {
                bundle.putBoolean("isFromDetail", true);//区分来自详情还是列表
                bundle.putString("strName", activityViewBean.getStrAtName());
                bundle.putString("strStartDate", activityViewBean.getStrStartDate());
                bundle.putString("strEndDate", activityViewBean.getStrEndDate());
                bundle.putString("strPlace", activityViewBean.getStrPlace());
                bundle.putString("strId", activityViewBean.getStrId());
                bundle.putString("strType", activityViewBean.getStrMtType());
                bundle.putString("state", Utils.getAttendST(intAttend));//出席状态
            } else if (meetingViewBean != null) {
                bundle.putBoolean("isFromDetail", true);//区分来自详情还是列表
                bundle.putString("strName", meetingViewBean.getStrMtName());
                bundle.putString("strStartDate", meetingViewBean.getStrStartDate());
                bundle.putString("strEndDate", meetingViewBean.getStrEndDate());
                bundle.putString("strPlace", meetingViewBean.getStrPlace());
                bundle.putString("strId", meetingViewBean.getStrId());
                bundle.putString("strType", meetingViewBean.getStrMtType());
                bundle.putString("state", Utils.getAttendST(intAttend));//出席状态
            }
            showActivity(fragment, MeetAct_SignUpActivity.class, bundle, 1);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_session:
                mSpinerPopWindow.setWidth(GIDensityUtil.dip2px(activity, 100));
                mSpinerPopWindow.showAsDropDown(session_TextView);
                break;
            case R.id.tv_search:
                llSearch.setVisibility(View.VISIBLE);
                search_TextView.setVisibility(View.GONE);
                tvCancel.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel_1:
                llSearch.setVisibility(View.INVISIBLE);
                search_TextView.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.GONE);
                if (GIStringUtil.isNotBlank(etSearch.getText().toString())) {
                    etSearch.setText("");
                    curPage = 1;
                    getActJoinMemInfo(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        myLetterView.setOnTouchingLetterChangedListener(new MyLetterView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                isLetterClick = true;
                // 该字母首次出现的位置
                int position = exLVAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    floatGroup_TextView.setVisibility(View.VISIBLE);
                    floatGroup_TextView.setText(s);
                    //exListView.setSelection(position);
                    expandableListView.setSelectedGroup(position);
                }
            }
        });

        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getActJoinMemInfo(false);
            }
        });

        searchAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ObjAttendMemBean joinMemSearchBean = (ObjAttendMemBean) searchAdapter.getItem(position);
                doItemClick(joinMemSearchBean);
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ObjAttendMemBean joinMemSearchBean = (ObjAttendMemBean) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                doItemClick(joinMemSearchBean);
                return false;
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        initRecyclerView();
        searchAttend_LinearLayout.setVisibility(View.VISIBLE);
        myLetterView.setVisibility(View.VISIBLE);
        objList = new ArrayList<>();
        objList.add(getString(R.string.string_tiled_display));
        objList.add(getString(R.string.string_sector));
        objList.add(getString(R.string.string_partisan));
        objList.add(getString(R.string.string_special_committee));
        session_TextView.setText(objList.get(0));
        mSpinerPopWindow = new SpinerPopWindow<String>(activity, objList, itemClickListener);
        expandableListView.setBackgroundColor(getResources().getColor(R.color.white));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        if (meetingViewBean != null) {
            if (meetingViewBean.getChildMeetList() != null && meetingViewBean.getChildMeetList().size() > 0) {
                childMtForWorkers = meetingViewBean.getChildMeetList();
                chiMtArray = new String[childMtForWorkers.size()];
                for (int i = 0; i < childMtForWorkers.size(); i++) {
                    chiMtArray[i] = childMtForWorkers.get(i).getStrMeetChildName();
                }
            }
        }
        myLetterView.setTextDialog(floatGroup_TextView);
        empty_TextView.setVisibility(View.GONE);
        getActJoinMemInfo(true);
        mOptionSearch = new OptionSearch(Looper.myLooper());
        mOptionSearch.setListener(this);
        expandableListView.setVisibility(View.VISIBLE);
    }


    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            session_TextView.setText(objList.get(position));
            if (strTypeCommissioner != position) {
                strTypeCommissioner = position;
                getActJoinMemInfo(true);
            }

        }
    };

    /**
     * 获取
     */
    public void getActJoinMemInfo(boolean isTrue) {
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        if (GIStringUtil.isNotBlank(intAttend)) {
            textParamMap.put("intAttend", intAttend);
        }
        if (GIStringUtil.isNotBlank(strKeyValue))
            textParamMap.put("strKeyValue", strKeyValue); // 查询关键字
        if (0 != strTypeCommissioner) {
            textParamMap.put("strType", strTypeCommissioner + "");
        }
        switch (sign) {
            case DefineUtil.hdgl:
                doRequestNormal(ApiManager.getInstance().getActAttendList(textParamMap), 0);
                break;
            case DefineUtil.hygl:
                doRequestNormal(ApiManager.getInstance().getMeetAttendList(textParamMap), 0);
                break;
        }
    }


    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                try {
                    AttendPersonListBean attendPersonListBean = (AttendPersonListBean) obj;
                    objPersonListBeen = attendPersonListBean.getObjList();
                    if (GIStringUtil.isNotBlank(strKeyValue)) {
                        search_recyclerView.setVisibility(View.VISIBLE);
                        expandableListView.setVisibility(View.GONE);
                        floatGroup_TextView.setVisibility(View.GONE);
                        myLetterView.setVisibility(View.GONE);
                        empty_TextView.setVisibility(View.GONE);
                        expandableListView.setVisibility(View.GONE);
                        if (curPage == 1) {
                            searchAdapter.setList(objPersonListBeen);
                        } else {
                            searchAdapter.addData(objPersonListBeen);
                        }
                        Utils.setLoadMoreViewState(attendPersonListBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, searchAdapter);
                        if (ProjectNameUtil.isSZSZX(activity) || ProjectNameUtil.isJMSZX(activity)) {
                            ((MeetAct_DetailActivity) activity).setPageTitle(1, "出席情况(" + attendPersonListBean.getIntMemTotleCount() + ")");
                            ((MeetAct_DetailActivity) activity).setPageTitle(2, "请假审核(" + attendPersonListBean.getIntMeetApplyCount() + ")");
                        }
                    } else {
                        search_recyclerView.setVisibility(View.GONE);
                        empty_TextView.setVisibility(View.GONE);
                        expandableListView.setVisibility(View.VISIBLE);
                        exLVAdapter = new AttendPerson_ExLVAdapter(activity, objPersonListBeen);
//                        exLVAdapter.setPaved(0 == strTypeCommissioner);
                        exLVAdapter.setPaved(true);
                        exLVAdapter.setIntModify(attendPersonListBean.getIntModify() == 1);
                        expandableListView.setAdapter(exLVAdapter);
                        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                return 0 == strTypeCommissioner;
                            }
                        });
                        if (0 == strTypeCommissioner) {
                            floatGroup_TextView.setVisibility(View.VISIBLE);
                            myLetterView.setVisibility(View.VISIBLE);
                        } else {
                            floatGroup_TextView.setVisibility(View.GONE);
                            myLetterView.setVisibility(View.GONE);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            expandableListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                    int tPostion = expandableListView.getFirstVisiblePosition();//得到当前显示的第一个item的position
                                    long flatPostion = expandableListView.getExpandableListPosition(tPostion);//转换为ExpandableListView特有的类似于position的东西
                                    //通过两个静态方法得到group(0)和child(-1)的position
                                    int groupPosition = ExpandableListView.getPackedPositionGroup(flatPostion);
                                    int childPosition = ExpandableListView.getPackedPositionChild(flatPostion);

                                    if (isLetterClick && groupPosition >= 0) {
                                        floatGroup_TextView.setVisibility(View.VISIBLE);
                                        floatGroup_TextView.setText(exLVAdapter.getGroupNames().get(groupPosition));
                                        GISharedPreUtil.setValue(activity, "groupPosition", groupPosition);
                                    } else {
                                        if ((0 == strTypeCommissioner) && ((groupPosition == 0 && childPosition == -1) || (groupPosition != -1 && (groupPosition != GISharedPreUtil.getInt(activity, "groupPosition"))))) {
                                            floatGroup_TextView.setVisibility(View.VISIBLE);
                                            floatGroup_TextView.setText(exLVAdapter.getGroupNames().get(groupPosition));
                                            GISharedPreUtil.setValue(activity, "groupPosition", groupPosition);
                                        } else if (groupPosition == -1) {
                                            floatGroup_TextView.setVisibility(View.GONE);
                                        }
                                    }
                                    isLetterClick = false;
                                }
                            });
                        }
                        int size = objPersonListBeen.size();
                        for (int i = 0; i < size; i++) {
                            expandableListView.expandGroup(i);
                        }
                        if (ProjectNameUtil.isSZSZX(activity) || ProjectNameUtil.isJMSZX(activity)) {
                            ((MeetAct_DetailActivity) activity).setPageTitle(1, "出席情况(" + attendPersonListBean.getIntMemTotleCount() + ")");
                            ((MeetAct_DetailActivity) activity).setPageTitle(2, "请假审核(" + attendPersonListBean.getIntMeetApplyCount() + ")");
                        }
                        if (exLVAdapter.getGroupCount() <= 0) {
                            empty_TextView.setVisibility(View.VISIBLE);
                            expandableListView.setVisibility(View.GONE);
                            floatGroup_TextView.setVisibility(View.GONE);
                            myLetterView.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    @Override
    public void getKeyword(String keyword) {
        strKeyValue = keyword;
        getActJoinMemInfo(false);
    }

    /**
     * 更新会议、活动详情参会情况页面
     */
    public void update() {
        getActJoinMemInfo(false);
    }
}
