package com.suncn.ihold_zxztc.activity.application.publicopinion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.Choose_JoinMemActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.SimilarProposalsActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.PublicOpinionListBean;
import com.suncn.ihold_zxztc.bean.SimilarProposalListBean;
import com.suncn.ihold_zxztc.bean.ThemeTypeBean;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MenuItemEditLayout;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import skin.support.content.res.SkinCompatResources;

/**
 * 社情民意提交页面
 */
public class PublicOpinion_AddActivity extends BaseActivity {
    @BindView(id = R.id.et_title)
    private GIEditText title_EditText;//信息标题
    @BindView(id = R.id.tv_jointMem, click = true)
    private MenuItemEditLayout jointMem_TextView; // 联名委员
    @BindView(id = R.id.tv_theme, click = true)
    private MenuItemEditLayout theme_TextView;
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮
    @BindView(id = R.id.et_content)
    private GIEditText content_EditText;//信息内容
    @BindView(id = R.id.tv_confirm, click = true)
    private TextView tvconFirm;
    @BindView(id = R.id.tv_temp, click = true)
    private TextView tvTemp;
    private String title;
    private String content;
    private String strChooseValueId = ""; // 选中的委员Id、机构Id
    @BindView(id = R.id.view_place)
    private View viewPlace;
    private ThemeTypeDialogList themeTypeDialogList;
    private String themeTypeId = "";
    private ArrayList<ThemeTypeBean.themeBean> themeList;
    private NormalListDialog normalListDialog;
    private String strId;
    private String state = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        strChooseValueId = bundle.getString("strChooseValueId");
                        GILogUtil.d(strChooseValueId);
                        jointMem_TextView.setValue(Utils.getShowAddress(strChooseValueId, false));
                    }
                    break;
                case 2:
                    setResult(RESULT_OK);
                    finish();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        setStatusBar();
        isShowBackBtn = true;
        setContentView(R.layout.activity_publicopinion_add);
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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // 标题名称
            String headTitle = bundle.getString("headTitle");
            strId = bundle.getString("strId", "");
            setHeadTitle(headTitle);
        }
        if (GIStringUtil.isNotBlank(strId)) {
            getTempMsg();
            goto_Button.setText(getResources().getString(R.string.font_clear_search));
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setTextColor(getResources().getColor(R.color.view_head_bg));
        }
        getThemeType();
        setHeadTitle("社情民意");
        viewPlace.setVisibility(View.GONE);
    }

    @Override
    public void setListener() {
        super.setListener();
        title_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.contains("\n")) {
                    text = text.replace("\n", "");
                    title_EditText.setText(text);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tv_mike:
                MikeEditTextUtil util = new MikeEditTextUtil(activity, content_EditText);
                util.viewShow();
                break;
            case R.id.tv_jointMem:
                bundle.putString("strChooseValueId", strChooseValueId);
                showActivity(activity, Choose_JoinMemActivity.class, bundle, 0);
                break;
            case R.id.tv_theme:
                showMyDialog();
                break;
            case R.id.tv_confirm:
                title = title_EditText.getText().toString().trim();
                content = content_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(title)) {
                    showToast(title_EditText.getHint().toString());
                    title_EditText.requestFocus();
                } else if (GIStringUtil.isBlank(themeTypeId)) {
                    showToast(theme_TextView.getHint());
                } else if (GIStringUtil.isBlank(content)) {
                    showToast(content_EditText.getHint().toString());
                    content_EditText.requestFocus();
                } else {
                    submitSqmy("1");
                }
                break;
            case R.id.tv_temp:
                title = title_EditText.getText().toString().trim();
                content = content_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(title)) {
                    showToast(title_EditText.getHint().toString());
                    title_EditText.requestFocus();
                    return;
                }
                title = title_EditText.getText().toString().trim();
                content = content_EditText.getText().toString().trim();
                submitSqmy("0");
                break;
            case R.id.btn_goto:
                showConfirmDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 对话框
     */
    private void showConfirmDialog() {
        String title;
        title = getString(R.string.string_are_you_sure_delect_pub);
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(title).btnText(getString(R.string.string_cancle), getString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                        delectServlet();


                    }
                }
        );
    }

    private void delectServlet() {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().InfoDeleteServlet(textParamMap), 3);
    }

    private void showMyDialog() {
        //父级数据
        String[] typeStringsP = new String[themeList.size()];
        for (int i = 0; i < themeList.size(); i++) {
            if (themeList.get(i).getObjChildList() != null && themeList.get(i).getObjChildList().size() > 0) {
                typeStringsP[i] = themeList.get(i).getStrTopicTypeName() + "   >";
            } else {
                typeStringsP[i] = themeList.get(i).getStrTopicTypeName();
            }
        }
        normalListDialog = new NormalListDialog(activity, typeStringsP);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (themeList.get(position).getObjChildList() != null && themeList.get(position).getObjChildList().size() > 0) {
                    //子集数据
                    String[] typeStrings = new String[themeList.get(position).getObjChildList().size()];
                    for (int j = 0; j < themeList.get(position).getObjChildList().size(); j++) {
                        typeStrings[j] = themeList.get(position).getObjChildList().get(j).getStrTopicTypeName();
                    }
                    normalListDialog.dismiss();
                    normalListDialog = new NormalListDialog(activity, typeStrings);
                    normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
                        @Override
                        public void onOperItemClick(AdapterView<?> parent, View view, int position1, long id) {
                            String text = typeStrings[position1];
                            theme_TextView.setValue(text);
                            themeTypeId = (themeList.get(position).getObjChildList().get(position1).getStrTopicType());
                            normalListDialog.dismiss();
                        }
                    });
                    normalListDialog.title(getString(R.string.picture_please_select) + getString(R.string.string_theme));
                    normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                    normalListDialog.show();
                } else {
                    String text = typeStringsP[position];
                    theme_TextView.setValue(text);
                    themeTypeId = (themeList.get(position).getStrTopicType());
                    normalListDialog.dismiss();
                }

            }
        });
        normalListDialog.title(getString(R.string.picture_please_select) + getString(R.string.string_theme));
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.show();
    }

    /**
     * 社情民意提交
     */
    private void submitSqmy(String type) {
        state = type;
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        if (GIStringUtil.isNotBlank(strId)) {
            textParamMap.put("strId", strId);
        }
        textParamMap.put("strTitle", title);
        textParamMap.put("strContent", content);
        textParamMap.put("strState", type);
        if (GIStringUtil.isNotBlank(strChooseValueId) && strChooseValueId.substring(strChooseValueId.length() - 2, strChooseValueId.length() - 1).equals(",")) {
            textParamMap.put("strJointMem", strChooseValueId.substring(0, strChooseValueId.length() - 1));
        } else {
            textParamMap.put("strJointMem", strChooseValueId);
        }
        textParamMap.put("strTopicType", themeTypeId);
        doRequestNormal(ApiManager.getInstance().sumbitPublicOpinionInfo(textParamMap), 0);
    }

    /**
     * 获取主题类型
     */
    private void getThemeType() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().InfoTypeAllListServlet(textParamMap), 1);
    }

    private void getTempMsg() {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().InfoAddViewServlet(textParamMap), 2);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String totastMessage = null;
        prgDialog.closePrgDialog();
        try {
            switch (what) {
                case 0:
                    SimilarProposalListBean similarProposalListBean = (SimilarProposalListBean) obj;
                    ArrayList<SimilarProposalListBean.SimilarProposal> similarProposals = similarProposalListBean.getObjList();
                    if (similarProposals != null && similarProposals.size() > 0) {
                        Bundle bundle = new Bundle();
                        //bundle.putParcelable("myValueMap", (Parcelable) myValueMap);
                        bundle.putString("strId",strId);
                        bundle.putString("title",title_EditText.getText().toString());
                        bundle.putString("content",content);
                        bundle.putString("strChooseValueId",strChooseValueId);
                        bundle.putSerializable("objList", similarProposals);
                        bundle.putString("strCheckMsg", similarProposalListBean.getStrCheckMsg());
                        bundle.putBoolean("isSaveData", similarProposalListBean.isSaveData());
                        bundle.putString("themeTypeId",themeTypeId);
                        showActivity(activity, SimilarOpinionActivity.class, bundle, 2);
                    }else {
                        if ("0".equals(state)) {
                            totastMessage = "暂存成功！";
                        } else {
                            totastMessage = "提交成功！";
                        }
                        setResult(RESULT_OK);
                        finish();
                    }

                    break;
                case 1:
                    ThemeTypeBean themeTypeBean = (ThemeTypeBean) obj;
                    themeList = themeTypeBean.getObjList();
                    break;
                case 2:
                    PublicOpinionListBean.ObjListBean detailBean = (PublicOpinionListBean.ObjListBean) obj;
                    title_EditText.setText(detailBean.getStrTitle());
                    content_EditText.setText(detailBean.getStrContent());
                    strChooseValueId = detailBean.getStrJointMem();
                    jointMem_TextView.setValue(Utils.getShowAddress(strChooseValueId, false));
                    themeTypeId = detailBean.getStrTopicType();
                    theme_TextView.setValue(detailBean.getStrTopicTypeName());
                    break;
                case 3:
                    showToast("社情民意删除成功");
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            totastMessage = getString(R.string.data_error);
        }
        if (totastMessage != null) {
            showToast(totastMessage);
        }
    }
}
