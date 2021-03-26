package com.suncn.ihold_zxztc.activity.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.google.gson.Gson;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDUser;
import com.longchat.base.util.QDGson;
import com.qd.longchat.activity.QDGroupChatActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qd.longchat.activity.QDCreateGroupActivity.REQUEST_CREATE_GROUP;


/**
 * 创建聊天室 聊天室内容修改
 */
public class Chat_AddRoomActivity extends BaseActivity {
    @BindView(id = R.id.ll_user)
    LinearLayout user_Layout;
    @BindView(id = R.id.et_opinion)
    EditText opinion_EditText;
    @BindView(id = R.id.tv_count)
    TextView count_TextView;
    @BindView(id = R.id.et_remark)
    EditText remark_EditText;
    @BindView(id = R.id.tv_remark_count)
    TextView remarkCount_TextView;
    @BindView(id = R.id.tv_addUser, click = true)
    TextView addUser_TextView;
    @BindView(id = R.id.tv_apply_name)
    TextView applyName_TextView;
    @BindView(id = R.id.ll_content)
    private LinearLayout llContent;
    private final int MAX_COUNT_DES = 20;
    private final int MAX_COUNT_REMARK = 200;
    private String strType = "0";
    private String strChooseValue = ""; // 选择的群成员的id
    private String naturalName; // 群聊名称
    private String strLinkId; // 修改群聊信息时 群聊id
    private String description; // 群介绍
    private List<QDUser> selectedUsers = new ArrayList<>();//启达选择的用户列表
    private List<Map<String, String>> userMapList = new ArrayList<>();
    private List<String> excludedIdList;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CREATE_GROUP:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            selectedUsers = (List<QDUser>) bundle.getSerializable(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
                            strChooseValue = "";
                            userMapList = new ArrayList<>();
                            for (QDUser user : selectedUsers) {
                                if (!user.getId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("user_id", user.getId());
                                    map.put("name", user.getName());
                                    userMapList.add(map);
                                }
                            }
                            strChooseValue = bundle.getString("strChooseValue");
                            applyName_TextView.setText(Utils.showAddress(strChooseValue));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_chat_add_room);
    }

    @Override
    public void initData() {
        super.initData();
        opinion_EditText.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(20)});
        remark_EditText.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(200)});
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setHeadTitle("修改群组信息");
            goto_Button.setText("保存");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            //goto_Button.setOnClickListener(this);
            strLinkId = bundle.getString("strLinkId");
            naturalName = bundle.getString("naturalName");
            description = bundle.getString("description");
            opinion_EditText.setText(naturalName);
            remark_EditText.setText(description);
            user_Layout.setVisibility(View.GONE);
            count_TextView.setText(naturalName.length() + "/" + MAX_COUNT_DES);
            remarkCount_TextView.setText(description.length() + "/" + MAX_COUNT_REMARK);
            strType = "5";
        } else {
            setHeadTitle("创建群组");
            goto_Button.setText("创建");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            user_Layout.setVisibility(View.VISIBLE);
            strType = "0";
            count_TextView.setText("0/" + MAX_COUNT_DES);
            opinion_EditText.setMaxEms(MAX_COUNT_DES);
            remarkCount_TextView.setText("0/" + MAX_COUNT_REMARK);
            remark_EditText.setMaxEms(MAX_COUNT_REMARK);
        }
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.refreshFontType(activity, "4");


    }

    InputFilter inputFilter = new InputFilter() {

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_/^[\\u4e00-\\u9fa5_a-zA-Z0-9\\s\\·\\~\\！\\@\\#\\￥\\%\\……\\&\\*\\（\\）\\——\\-\\+\\=\\【\\】\\{\\}\\、\\|\\；\\‘\\’\\：\\“\\”\\《\\》\\？\\，\\。\\、\\`\\~\\!\\#\\$\\%\\^\\&\\*\\(\\)\\_\\[\\]{\\}\\\\\\|\\;\\'\\'\\:\\\"\\\"\\,\\.\\/\\<\\>\\?]+$/]");
       /* // 英文标点
        private Pattern patternEn = Pattern.compile("^[`~!@#\$%^&*()_\\-+=<>?:\"{},.\\\\/;'\\[\\]]\$");
        // 中文标点
        private Pattern patternCn = Pattern.compile("^[·！#￥（——）：；“”‘、，|《。》？、【】\\[\\]]\$]");*/
        //Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (!matcher.find()) {
                return null;
            } else {
                showToast("只能输入汉字,英文，数字");
                return "";
            }

        }
    };


    @Override
    public void setListener() {
        super.setListener();
        opinion_EditText.addTextChangedListener(new TextWatcher() { //对EditText进行监听
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                count_TextView.setText(editable.length() + "/" + MAX_COUNT_DES);
                //如果EditText中的数据不为空，且长度大于指定的最大长度
                if (editable.length() > MAX_COUNT_DES) {
                    showToast("超出最大字数限制！");
                }
            }
        });
        remark_EditText.addTextChangedListener(new TextWatcher() { //对EditText进行监听
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                remarkCount_TextView.setText(editable.length() + "/" + MAX_COUNT_REMARK);
                //如果EditText中的数据不为空，且长度大于指定的最大长度
               /* if (editable.length() > MAX_COUNT_REMARK) {
                    //删除指定长度之后的数据
                    showToast("超出最大字数限制！");
                }*/
            }
        });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (view.getId()) {
            case R.id.btn_goto:
                String strName = opinion_EditText.getText().toString().trim();
                description = remark_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(strName)) {
                    showToast("请输入群名称");
                }
                if ((GIStringUtil.isBlank(strChooseValue) && "0".equals(strType))) {
                    showToast("请选择群成员");
//                BaseUtils.closeSoftInput(activity);
                } else {
                    prgDialog.showLoadDialog();
                    createGroup();
                }
                break;
            case R.id.tv_addUser:
                Bundle bundle = new Bundle();
                bundle.putInt("chooseType", 5);
                bundle.putBoolean("isSingle", false);
                bundle.putBoolean("isChoose",true);
                bundle.putString("strChooseValue", strChooseValue);
                showActivity(activity, Contact_MainActivity.class, bundle, REQUEST_CREATE_GROUP);
                break;
        }
    }


    /**
     * 启达群组新增
     */
    private void createGroup() {
        Gson gson = QDGson.getGson();
        String members = gson.toJson(userMapList);

        QDClient.getInstance().createGroup(opinion_EditText.getText().toString().trim(), members, "", new QDResultCallBack<String>() {
            @Override
            public void onError(String errorMsg) {
                GILogUtil.e(errorMsg);
            }

            @Override
            public void onSuccess(String groupId) {
                prgDialog.closePrgDialog();
                Intent groupIntent = new Intent(activity, QDGroupChatActivity.class);
                groupIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, groupId);
                startActivity(groupIntent);
                finish();
            }
        });
    }


}
