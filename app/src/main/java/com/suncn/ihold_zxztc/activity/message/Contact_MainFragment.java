package com.suncn.ihold_zxztc.activity.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.longchat.base.QDClient;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDGroupHelper;
import com.qd.longchat.activity.QDGroupActivity;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.Contact_JG_TreeLVAdapter;
import com.suncn.ihold_zxztc.adapter.Contact_Search_LVAdapter;
import com.suncn.ihold_zxztc.adapter.Contact_WY_ExLVAdapter;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.bean.JointMemSearchListBean;
import com.suncn.ihold_zxztc.bean.MyUnit;
import com.suncn.ihold_zxztc.bean.ZxtaJointMemListBean;
import com.suncn.ihold_zxztc.treelist.Node;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.view.MyLetterView;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.forscrollview.MyScrollExpandableListView;
import com.suncn.ihold_zxztc.view.forscrollview.ScrollExpandableListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * 通讯录/委员信息（查看履职）/选择人员Fragment
 */
public class Contact_MainFragment extends BaseFragment {
    @BindView(id = R.id.rl_head)
    private RelativeLayout head_RelativeLayout;
    @BindView(id = R.id.myLetterView, click = true)
    private MyLetterView myLetterView;
    @BindView(id = R.id.tv_head_title, click = true)
    private GITextView title_TextView;
    @BindView(id = R.id.tv_spinner)
    private GITextView spinner_TextView;
    @BindView(id = R.id.lv_search)
    private ListView search_ListView;//搜索结果列表
    @BindView(id = R.id.lv_recent_contact)
    private ListView jg_ListView;//机关通讯录列表ListView
    @BindView(id = R.id.exListview)
    private MyScrollExpandableListView wy_ExListView;
    @BindView(id = R.id.rl_contact)
    private RelativeLayout wyContact_Layout;
    @BindView(id = R.id.tv_empty)
    private ImageView empty_TextView;
    @BindView(id = R.id.et_search_bt)
    private ClearEditText search_EditText;//搜索框
    @BindView(id = R.id.ll_search_bt)
    private LinearLayout search_Layout;
    @BindView(id = R.id.tv_float_group)
    private TextView floatGroup_TextView;

    private Contact_WY_ExLVAdapter wyAdapter;
    private Contact_JG_TreeLVAdapter jgAdapter;
    private Contact_Search_LVAdapter searchAdapter;
    private View view;//添加的顶部view

    private List<Node> mDatas; // 通讯录数据集合
    private List<ZxtaJointMemListBean.ZxtaJointMemBean> wyContactList;
    private ArrayList<MyUnit> jgContactList;

    private boolean isWYContact = true; // 是否为委员通讯录
    private boolean isChoose = false; // 是否是选择操作
    private boolean isCreateGroup = false;
    private boolean isAddMember = false;//是否为邀请新成员入群
    private boolean isCommissioner = false; // 委员管理，查看委员履职
    private boolean isShowHead = false; // 是否显示头部四个快捷入口
    private boolean isShowGroupLayout; // 是否显示我的群组快捷入口
    private boolean isLetterClick = false;
    private String strKeyValue = ""; // 搜索关键字
    private String strChooseValue = ""; // 选中的委员
    private String strLinkId = "";//群id
    private String strUserType = "0";//0为委员 1为机关委
    private int intGroupCount;//群组数量
    private SpinerPopWindow<String> mSpinerPopWindow;
    private ArrayList<String> menuList = new ArrayList<>();
    private String strUrl = "";
    private List<QDUser> selectUserList = new ArrayList<>(); //视频会议已选的人员列表;
    private boolean isSingle;//是否是单选
    private boolean isOnlyWY;//是否是只选择委员
    private String showTitle;
    private boolean isAddGroup;
    private String strFilterUserIds = "";

    @Override
    public void onResume() {
        super.onResume();
        search_EditText.setText("");
        if (view != null) {
            if (!isShowGroupLayout || !AppConfigUtil.isUseQDIM(activity)) {
                view.findViewById(R.id.ll_group).setVisibility(View.GONE);
            } else {
                ((TextView) view.findViewById(R.id.tv_count)).setText("(" + QDGroupHelper.getGroupCount() + ")");
            }
        }
        //后期优化，不建议在此处实现刷新，因为activity回调在onResume之前
        if (DefineUtil.isNeedRefresh) {
            isWYContact = true;
            doMyRequest();
            DefineUtil.isNeedRefresh = false;
        }
        if (null != head_title_TextView && "履职".equals(head_title_TextView.getText().toString().trim())) {
            head_title_TextView.setTextColor(getResources().getColor(R.color.white));
            head_RelativeLayout.setBackgroundColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        }
    }

    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (activity.getClass() == Contact_MainActivity.class) {
            isShowBackBtn = true;
        }
        return inflater.inflate(R.layout.activity_contact_main, null);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        search_Layout.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = activity.getIntent().getExtras();
        }
        if (bundle != null) {
            isShowHead = bundle.getBoolean("isShowHead", false);
            isCommissioner = bundle.getBoolean("isCommissioner", false);
            isChoose = bundle.getBoolean("isChoose", false);
            isChoose = bundle.getBoolean("isChoose", false);
            isAddMember = bundle.getBoolean("isAddMember", false);
            isAddGroup = bundle.getBoolean("isAddGroup", false);
            strFilterUserIds = bundle.getString("strFilterUserIds", "");
            if (isAddMember) {
                strLinkId = bundle.getString("strLinkId");
            }
            isCreateGroup = bundle.getBoolean("isCreateGroup", false);
            strChooseValue = bundle.getString("strChooseValue", "");
            selectUserList = (List<QDUser>) bundle.getSerializable(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
            showTitle = bundle.getString("showTitle", "");
            isSingle = bundle.getBoolean("isSingle", false);
            isOnlyWY = bundle.getBoolean("isOnlyWY", false);
            if (isChoose) {
                setHeadTitle(showTitle);
                goto_Button.setText(getString(R.string.string_confirm));
                goto_Button.setVisibility(View.VISIBLE);
                goto_Button.refreshFontType(activity, "2");
                goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            if (showTitle.equals("履职")) { // 履职
                setHeadTitle(showTitle);
                isShowGroupLayout = false;
                head_title_TextView.setTextColor(getResources().getColor(R.color.white));
                head_RelativeLayout.setBackgroundColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
            } else if (showTitle.equals("委员信息")) { // 委员信息
                setHeadTitle(showTitle);
                isShowGroupLayout = false;
            } else {
                setHeadTitle(getString(R.string.string_committee_directory));
                isShowGroupLayout = true;
                if (!isOnlyWY) {
                    spinner_TextView.setVisibility(View.VISIBLE);
                    menuList = new ArrayList<>();
                    menuList.add(getString(R.string.string_committee_directory));
                    menuList.add(getString(R.string.string_institutional_address_book));
                    mSpinerPopWindow = new SpinerPopWindow<String>(activity, menuList, spinnerItemClickListener);
                }
            }
            if (isShowHead) {
                myLetterView.setVisibility(View.VISIBLE);
            }

            doMyRequest();
        }
    }

    /**
     * 按需进行请求操作
     */
    private void doMyRequest() {
        search_EditText.setText("");
        search_EditText.setHint(R.string.string_please_enter_a_name_or_username);
        if (isWYContact) {
            strUserType = "0";
            getListData(true);
            myLetterView.setTextDialog(floatGroup_TextView);
        } else {
            //search_EditText.setHint("输入单位名称");
            strUserType = "1";
            getListData(true);
        }
    }

    public void setJoinMeetUser(QDUser user) {
        selectUserList.add(user);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tv_head_title: // 头部委员通讯录/机关通讯录切换按钮
                if (spinner_TextView.getVisibility() == View.VISIBLE) {
                    mSpinerPopWindow.setWidth(title_TextView.getWidth() + GIDensityUtil.dip2px(activity, 20));
                    mSpinerPopWindow.showAsDropDown(title_TextView);
                }
                break;
            case R.id.btn_goto: // 搜索/选人时的确定按钮
                if (GIStringUtil.isNotBlank(strKeyValue)) { // 搜索
//                    if (searchAdapter != null) {
//                        strChooseValue = searchAdapter.getStrChooseValue();
//                    }
                    search_EditText.setText("");
                } else { // 选人
                    if (isWYContact) {
                        if (wyAdapter != null) {
                            strChooseValue = wyAdapter.getStrChooseValue();
                            selectUserList = wyAdapter.getSelectUserList();
                        }
                    } else {
                        if (jgAdapter != null) {
                            strChooseValue = jgAdapter.getStrChooseValue();
                            selectUserList = jgAdapter.getSelectUserList();
                        }
                    }
                    GILogUtil.i("strChooseValue==" + strChooseValue);
                    bundle = new Bundle();
                    bundle.putString("strChooseValue", strChooseValue);
                    bundle.putParcelableArrayList(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) selectUserList);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
                break;
            case R.id.ll_group: // 我的群组
                if (AppConfigUtil.isUseQDIM(activity) && QDClient.getInstance().isOnline()) {
                    showActivity(fragment, QDGroupActivity.class);
                } else {
                    showToast(getString(R.string.string_im_login_failed_please_relogin));
                }
                break;
            case R.id.ll_sector:
                bundle = new Bundle();
                bundle.putString("strType", "1");
                bundle.putString("headTitle", getString(R.string.string_sector));
                bundle.putBoolean("isCommissioner", isCommissioner);
                showActivity(fragment, Contact_TypeListActivity.class, bundle);
                break;
            case R.id.ll_party:
                bundle = new Bundle();
                bundle.putString("strType", "2");
                bundle.putString("headTitle", getString(R.string.string_partisan));
                bundle.putBoolean("isCommissioner", isCommissioner);
                showActivity(fragment, Contact_TypeListActivity.class, bundle);
                break;
            case R.id.ll_branch:
                bundle = new Bundle();
                bundle.putString("strType", "3");
                bundle.putString("headTitle", getString(R.string.string_special_committee));
                bundle.putBoolean("isCommissioner", isCommissioner);
                showActivity(fragment, Contact_TypeListActivity.class, bundle);
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    @Override
    public void setListener() {
        myLetterView.setOnTouchingLetterChangedListener(new MyLetterView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                isLetterClick = true;
                // 该字母首次出现的位置
                int position = wyAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    floatGroup_TextView.setVisibility(View.VISIBLE);
                    floatGroup_TextView.setText(s);
                    wy_ExListView.setSelectedGroup(position);
                }
            }
        });
        wy_ExListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        wy_ExListView.setScrollViewListener(new ScrollExpandableListener() {
            @Override
            public void onScrollChanged(MyScrollExpandableListView scrollView, int x, int y, int oldx, int oldy) {
                int tPostion = wy_ExListView.getFirstVisiblePosition();//得到当前显示的第一个item的position
                long flatPostion = wy_ExListView.getExpandableListPosition(tPostion);//转换为ExpandableListView特有的类似于position的东西
                //通过两个静态方法得到group(0)和child(-1)的position
                int groupPosition = ExpandableListView.getPackedPositionGroup(flatPostion);
                int childPosition = ExpandableListView.getPackedPositionChild(flatPostion);
                if (isLetterClick && groupPosition >= 0) {
                    floatGroup_TextView.setVisibility(View.VISIBLE);
                    floatGroup_TextView.setText(wyAdapter.getGroupNames().get(groupPosition));
                    GISharedPreUtil.setValue(activity, "groupPosition", groupPosition);
                } else {
                    if ((groupPosition == 0 && childPosition == -1) || (groupPosition != -1 && (groupPosition != GISharedPreUtil.getInt(activity, "groupPosition")))) {
                        floatGroup_TextView.setVisibility(View.VISIBLE);
                        floatGroup_TextView.setText(wyAdapter.getGroupNames().get(groupPosition));
                        GISharedPreUtil.setValue(activity, "groupPosition", groupPosition);
                    } else if (groupPosition == -1) {
                        floatGroup_TextView.setVisibility(View.GONE);
                    }
                }
                isLetterClick = false;
            }
        });
        search_EditText.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                doSearch();
            }
        });
        super.setListener();
    }

    /**
     * popupwindow显示的ListView的item点击事件
     */
    AdapterView.OnItemClickListener spinnerItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            title_TextView.setText(menuList.get(position));
            isWYContact = position == 0;
            if (isWYContact) {
                if (jgAdapter != null) {
                    strChooseValue = jgAdapter.getStrChooseValue();
                    selectUserList = jgAdapter.getSelectUserList();
                }
            } else {
                if (wyAdapter != null) {
                    strChooseValue = wyAdapter.getStrChooseValue();
                    selectUserList = wyAdapter.getSelectUserList();
                }
            }
            doMyRequest();
        }
    };

    /**
     * 添加头部快捷入口（我的群组、党派、界别、专委会）
     */
    private void addHeadView() {
        if (isShowHead) {
            if (view != null) {
                wy_ExListView.removeHeaderView(view);
            }
            view = LayoutInflater.from(activity).inflate(R.layout.view_head_contact, null);
            ((TextView) view.findViewById(R.id.tv_count)).setText("(" + QDGroupHelper.getGroupCount() + ")");
            view.findViewById(R.id.ll_group).setOnClickListener(this);
            if (!isShowGroupLayout || !AppConfigUtil.isUseQDIM(activity)) {
                view.findViewById(R.id.ll_group).setVisibility(View.GONE);
            }
            if (GISharedPreUtil.getInt(activity, "intAdmin") == 1 && isCommissioner) {
                view.findViewById(R.id.ll_sector).setVisibility(View.GONE);
                view.findViewById(R.id.ll_party).setVisibility(View.GONE);
            }
            if (ProjectNameUtil.isGZSZX(activity) && !showTitle.equals("履职")) {  //贵州省政协 委员通讯录只保留‘界别’一个筛选信息
                view.findViewById(R.id.ll_branch).setVisibility(View.GONE);
                view.findViewById(R.id.ll_party).setVisibility(View.GONE);
            }
            view.findViewById(R.id.ll_sector).setOnClickListener(this);
            view.findViewById(R.id.ll_party).setOnClickListener(this);
            view.findViewById(R.id.ll_branch).setOnClickListener(this);
            wy_ExListView.addHeaderView(view);
        }
    }

    /**
     * 执行搜索操作
     */
    private void doSearch() {
        if (GIStringUtil.isNotBlank(strKeyValue)) { // 执行搜索
            search_ListView.setVisibility(View.VISIBLE);
            wyContact_Layout.setVisibility(View.GONE);
            jg_ListView.setVisibility(View.GONE);
            getSearchList();
        } else { // 取消搜索
            search_ListView.setVisibility(View.GONE);
            empty_TextView.setVisibility(View.GONE);
            if (isWYContact) {
                wyContact_Layout.setVisibility(View.VISIBLE);
                if (searchAdapter != null) {
                    strChooseValue = searchAdapter.getStrChooseValue();
                    wyContactList = wyAdapter.getZxtaJointMemBeans();
                    wyAdapter.setSelectUserList(searchAdapter.getSelectUserList());
                    doWyCheck();
                }
            } else {
                jg_ListView.setVisibility(View.VISIBLE);
                if (searchAdapter != null) {
                    strChooseValue = searchAdapter.getStrChooseValue();
                    jgAdapter.setSelectUserList(searchAdapter.getSelectUserList());
                    selectUserList = searchAdapter.getSelectUserList();
                    jgRequestResult(null, jgContactList, "");
                }
            }
        }
    }

    /**
     * 查询列表
     */
    private void getSearchList() {
        textParamMap = new HashMap<>();
        textParamMap.put("strKeyValue", strKeyValue); // 查询关键字
        textParamMap.put("strUserType", strUserType); //0为委员 1为机关委
        textParamMap.put("strFilterUserIds", strFilterUserIds);
        if (isAddMember) { // 邀请新成员入群
            textParamMap.put("strHandlerType", "1"); // 群组 为 1
            textParamMap.put("strLinkId", strLinkId); // 群组 选人 传值过滤
        }
        if (isCreateGroup) { // 创建群组
            textParamMap.put("strHandlerType", "1");
        }
        if (isCommissioner) {
            doRequestNormal(ApiManager.getInstance().getMemList(textParamMap), 3);
        } else {
            doRequestNormal(ApiManager.getInstance().getContactSearchList(textParamMap), 3);
        }
    }

    /**
     * 委员列表
     */
    private void getListData(boolean isTrue) {
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "");//""为平铺展示,"1"为界别,"2"为党派,"3"为专委会
        textParamMap.put("strUserType", strUserType); //0为委员 1为机关委
        textParamMap.put("strFilterUserIds", strFilterUserIds);
        if (isAddMember) { // 邀请新成员入群
            textParamMap.put("strHandlerType", "1"); // 群组 为 1
            textParamMap.put("strLinkId", strLinkId); // 群组 选人 传值过滤
        }
        if (isCreateGroup) { // 创建群组
            textParamMap.put("strHandlerType", "1");
        }
        if (isCommissioner) {
            doRequestNormal(ApiManager.getInstance().getMemDutyList(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().getContactList(textParamMap), 0);
        }
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
//        try {
        switch (what) {
            case 3://查询列表
                JointMemSearchListBean searchPersonUnitListBean = (JointMemSearchListBean) obj;
                ArrayList<BaseUser> objList = searchPersonUnitListBean.getObjList();
                String[] letters = new String[objList.size()];
                for (int i = 0; i < objList.size(); i++) {
                    letters[i] = objList.get(i).getStrName();
                }
                myLetterView.setLetters(letters);
                if (objList != null && objList.size() > 0) {
                    floatGroup_TextView.setVisibility(View.GONE);
                    if (isWYContact) {
                        if (wyAdapter != null) {
                            strChooseValue = wyAdapter.getStrChooseValue();
                        }
                    } else {
                        if (jgAdapter != null) {
                            strChooseValue = jgAdapter.getStrChooseValue();
                        }
                    }
                    if (GIStringUtil.isNotBlank(strChooseValue)) {
                        for (int i = 0; i < objList.size(); i++) {
                            if (strChooseValue.contains(objList.get(i).getStrUserId())) {
                                objList.get(i).setChecked(true);
                            }
                        }
                    }
                    if (searchAdapter == null) {
                        searchAdapter = new Contact_Search_LVAdapter(activity, objList, isChoose);
                        searchAdapter.setStrUrl(searchPersonUnitListBean.getStrUrl());
                        searchAdapter.setStrChooseValue(strChooseValue);
                        search_ListView.setAdapter(searchAdapter);
                    } else {
                        searchAdapter.setStrUrl(searchPersonUnitListBean.getStrUrl());
                        searchAdapter.setStrChooseValue(strChooseValue);
                        searchAdapter.setsearchPersonUnitLists(objList);
                        search_ListView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }
                    searchAdapter.setCommissioner(isCommissioner);
                } else {
                    search_ListView.setVisibility(View.GONE);
                    empty_TextView.setVisibility(View.VISIBLE);
                    empty_TextView.setImageResource(R.mipmap.bg_search_empty);
                }
                break;
            case 0:
                prgDialog.closePrgDialog();
                ZxtaJointMemListBean zxtaJointMemListBean = (ZxtaJointMemListBean) obj;
                intGroupCount = zxtaJointMemListBean.getIntRoomSizeByLoginUser();
                wyContactList = zxtaJointMemListBean.getObjList();
                jgContactList = zxtaJointMemListBean.getObjUnits_list();
                if (isWYContact) {
                    if (wyContactList != null && wyContactList.size() > 0) {
                        empty_TextView.setVisibility(View.GONE);
                        jg_ListView.setVisibility(View.GONE);
                        wyContact_Layout.setVisibility(View.VISIBLE);
                        wyRequestResult(zxtaJointMemListBean.getStrUrl());
                    } else {
                        wyContact_Layout.setVisibility(View.GONE);
                        jg_ListView.setVisibility(View.GONE);
                        empty_TextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (jgContactList != null && jgContactList.size() > 0) {
                        empty_TextView.setVisibility(View.GONE);
                        wyContact_Layout.setVisibility(View.GONE);
                        jg_ListView.setVisibility(View.VISIBLE);
                        jgRequestResult(null, jgContactList, zxtaJointMemListBean.getStrUrl());
                    } else {
                        wyContact_Layout.setVisibility(View.GONE);
                        jg_ListView.setVisibility(View.GONE);
                        empty_TextView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
                break;
        }
//        } catch (Exception e) {
//            e.printStackTrace();
//            toastMessage = getString(R.string.data_error);
//        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    /**
     * 初始化委员通讯录数据
     */
    private void wyRequestResult(String strUrl) {
        wyAdapter = new Contact_WY_ExLVAdapter(activity, wyContactList, isChoose); // 必须重新new，否则ExpandableListView刷新会出问题
        wyAdapter.setStrUrl(strUrl);
        wyAdapter.setPaved(true);
        wyAdapter.setStrChooseValue(strChooseValue);
        wyAdapter.setCommissioner(isCommissioner);
        wyAdapter.setSingle(isSingle);
        if (selectUserList != null) {
            wyAdapter.setSelectUserList(selectUserList);
        }
        addHeadView();
        wy_ExListView.setAdapter(wyAdapter);
        for (int i = 0; i < wyAdapter.getGroupCount(); i++) {
            wy_ExListView.expandGroup(i);
        }
        if (GIStringUtil.isNotBlank(strChooseValue)) {
            doWyCheck();
        }
    }

    /**
     * 处理委员通讯录选中数据
     */
    private void doWyCheck() {
        ZxtaJointMemListBean.ZxtaJointMemBean zxtaJointMemBean;
        for (int i = 0; i < wyContactList.size(); i++) {
            zxtaJointMemBean = wyContactList.get(i);
            ArrayList<BaseUser> mem_list = zxtaJointMemBean.getObjChildList();
            if (mem_list != null) {
                BaseUser memListBean;
                for (int j = 0; j < mem_list.size(); j++) {
                    memListBean = mem_list.get(j);
                    memListBean.setChecked(false);
                    if (strChooseValue.contains(memListBean.getStrUserId())) { // 判断是否已选中
                        memListBean.setChecked(true);
                        mem_list.set(j, memListBean);
                    }
                }
                zxtaJointMemBean.setObjChildList(mem_list);
                wyContactList.set(i, zxtaJointMemBean);
            }
        }
        wyAdapter.setZxtaJointMemBeans(wyContactList);
        wyAdapter.setStrChooseValue(strChooseValue);
        wyAdapter.notifyDataSetChanged();
    }


    /**
     * 初始化机关通讯录数据
     */
    public void jgRequestResult
    (ArrayList<BaseUser> objUsers_list, ArrayList<MyUnit> personUnits, String url) {
        mDatas = new ArrayList<Node>();
        if (objUsers_list != null) {
            BaseUser baseUser;
            for (int j = 0; j < objUsers_list.size(); j++) {
                baseUser = objUsers_list.get(j);
                if (strChooseValue.contains(baseUser.getStrUserId())) { // 判断是否已选中
                    mDatas.add(new Node("-1", baseUser, true));
                } else {
                    mDatas.add(new Node("-1", baseUser, false));
                }
            }
        }
        initListData(null, personUnits);
        jgAdapter = new Contact_JG_TreeLVAdapter(isChoose, jg_ListView, activity, mDatas, 1);
        jgAdapter.setStrChooseValue(strChooseValue);
        jgAdapter.setStrUrl(url);
        jgAdapter.setSelectUserList(selectUserList);
        jg_ListView.setAdapter(jgAdapter);
    }

    /**
     * 初始化多级列表数据
     */
    private void initListData(MyUnit mPersonUnit, ArrayList<MyUnit> personUnits) {
        if (personUnits != null) {
            String pId = "-1";
            if (mPersonUnit != null) {
                pId = mPersonUnit.getStrUnit_id();
            }
            MyUnit personUnit;
            for (int i = 0; i < personUnits.size(); i++) {
                personUnit = personUnits.get(i);
                mDatas.add(new Node(pId, personUnit, false));

                ArrayList<BaseUser> baseUsers = personUnit.getObjUsers_list();
                if (baseUsers != null && baseUsers.size() > 0) {
                    BaseUser baseUser;
                    for (int j = 0; j < baseUsers.size(); j++) {
                        baseUser = baseUsers.get(j);
                        if (strChooseValue.contains(baseUser.getStrUserId())) { // 判断是否已选中
                            mDatas.add(new Node(personUnit.getStrUnit_id(), baseUser, true));
                        } else {
                            mDatas.add(new Node(personUnit.getStrUnit_id(), baseUser, false));
                        }
                    }
                }

                ArrayList<MyUnit> personUnits1 = personUnit.getObjUnits_list();
                if (personUnits1 != null && personUnits1.size() > 0) {
                    initListData(personUnit, personUnits1);
                }
            }
        }
    }


}
