package com.qd.longchat.order.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDOrderHelper;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.manager.QDOrderManager;
import com.longchat.base.model.QDOrder;
import com.longchat.base.model.gd.QDFileModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.activity.QDSelectPhotoActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.holder.QDBaseInfoHolder;
import com.qd.longchat.model.QDOrderTypeModel;
import com.qd.longchat.order.QDOrderSelectFragment;
import com.qd.longchat.order.QDOrderUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDTimeSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/21 上午9:32
 */
public class QDOrderSendActivity extends QDBaseActivity {

    private final static int REQUEST_FOR_ADD_USER = 1000;
    private final static int REQUEST_FOR_ADD_PIC = 1001;
    private final static int REQUEST_FOR_ADD_FILE = 1002;

    private final static int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;

    @BindView(R2.id.view_os_title)
    View viewTitle;
    @BindView(R2.id.view_os_type)
    View viewType;
    @BindView(R2.id.view_os_level)
    View viewLevel;
    @BindView(R2.id.et_os_title)
    EditText etTitle;
    @BindView(R2.id.et_os_content)
    EditText etContent;
    @BindView(R2.id.view_os_add_pic)
    View viewAddPic;
    @BindView(R2.id.view_os_add_file)
    View viewAddFile;
    @BindView(R2.id.view_os_select_user)
    View viewSelectUser;
    @BindView(R2.id.view_os_select_date)
    View viewSelectDate;
    @BindView(R2.id.ll_os_pic_layout)
    LinearLayout llPicLayout;
    @BindView(R2.id.ll_os_file_layout)
    LinearLayout llFileLayout;

    @BindString(R2.string.im_text_send_order)
    String strTitle;
    @BindString(R2.string.im_text_order_issue)
    String strIssue;
    @BindString(R2.string.im_text_order_type_tag)
    String strTypeTag;
    @BindString(R2.string.im_text_order_level_tag)
    String strLevelTag;
    @BindString(R2.string.im_text_order_add_pic_tag)
    String strAddPicTag;
    @BindString(R2.string.im_text_order_add_file_tag)
    String strAddFileTag;
    @BindString(R2.string.im_text_order_select_user_tag)
    String strSelectUserTag;
    @BindString(R2.string.im_text_order_select_date_tag)
    String strSelectDateTag;

    private Context context;
    private List<String> selectedIds;
    private List<QDUser> selectedUsers;
    private String selectedNames;
    private QDTimeSelector selector;
    private TextView tvTypeInfo, tvLevelInfo, tvSelectUserInfo, tvSelectDateInfo;
    private List<QDOrderTypeModel> typeList;
    private String[] levels = new String[]{"一般", "重要", "紧急"};
    private String recvsStr;
    private List<Map<String, String>> picMapList, fileMapList;
    private Gson gson;
    private int selectedTypeIndex;
    private int selectedLevel = 2;

    private QDOrderSelectFragment.OnSelectClickListener listener = new QDOrderSelectFragment.OnSelectClickListener() {
        @Override
        public void onSelectClick(int type, int position) {
            if (type == QDOrderSelectFragment.TYPE_OF_TYPE) {
                tvTypeInfo.setText(typeList.get(position).getTypeName());
                selectedTypeIndex = position;
            } else {
                tvLevelInfo.setText(levels[position]);
                selectedLevel = position + 1;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_send);
        ButterKnife.bind(this);
        selectedIds = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        context = this;
        gson = new Gson();
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText(strIssue);
        picMapList = new ArrayList<>();
        fileMapList = new ArrayList<>();
        typeList = QDOrderUtil.getInstance().getOrderTypeList();
        if (typeList == null || typeList.size() == 0) {
            QDUtil.showToast(context, "指令类型为空");
            finish();
        }
        selector = new QDTimeSelector(this, QDTimeSelector.FORMAT.DATE_TIME, new QDTimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                //修改服务器上生日,修改本地生日
                tvSelectDateInfo.setText(time);
            }
        }, new Date(), QDTimeSelector.StringToDate(QDTimeSelector.END_DATE, QDTimeSelector.FORMAT.DATE_TIME.value));
        initUI();
    }

    private void initUI() {
        initType();
        initLevel();
        initAddPic();
        initAddFile();
        initSelectUser();
        initSelectDate();
    }

    private void initType() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewType);
        holder.tvItemName.setText(strTypeTag);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        tvTypeInfo = holder.tvItemInfo;
        tvTypeInfo.setText(typeList.get(0).getTypeName());
    }

    private void initLevel() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewLevel);
        holder.tvItemName.setText(strLevelTag);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        tvLevelInfo = holder.tvItemInfo;
        tvLevelInfo.setText("重要");
    }

    private void initAddPic() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewAddPic);
        holder.tvItemName.setText(strAddPicTag);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemArrow.setImageResource(R.drawable.ic_order_pic);
    }

    private void initAddFile() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewAddFile);
        holder.tvItemName.setText(strAddFileTag);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemArrow.setImageResource(R.drawable.ic_order_file);
    }

    private void initSelectUser() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewSelectUser);
        holder.tvItemName.setText(strSelectUserTag);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        tvSelectUserInfo = holder.tvItemInfo;
        tvSelectUserInfo.setText("请选择");
    }

    private void initSelectDate() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewSelectDate);
        holder.tvItemName.setText(strSelectDateTag);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        tvSelectDateInfo = holder.tvItemInfo;
    }

    @OnClick({R2.id.view_os_type, R2.id.view_os_level, R2.id.view_os_select_user, R2.id.view_os_select_date
            , R2.id.tv_title_right, R2.id.view_os_add_pic, R2.id.view_os_add_file})
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.view_os_type) {
            ArrayList<String> list = new ArrayList<>();
            for (QDOrderTypeModel model : typeList) {
                list.add(model.getTypeName());
            }
            showDialog(QDOrderSelectFragment.TYPE_OF_TYPE, list);

        } else if (i1 == R.id.view_os_level) {
            showDialog(QDOrderSelectFragment.TYPE_OF_LEVEL, new ArrayList(Arrays.asList(levels)));

        } else if (i1 == R.id.view_os_select_user) {
            List<String> excludedList = new ArrayList<>();
            excludedList.add(QDLanderInfo.getInstance().getId());
            Intent i = new Intent(this, QDContactActivity.class);
            i.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_MULTI);
            i.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST, (ArrayList<String>) excludedList);
            if (selectedIds.size() != 0) {
                i.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectedIds);
//                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_NAMES, selectedNames);
                i.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) selectedUsers);
            }
            startActivityForResult(i, REQUEST_FOR_ADD_USER);

        } else if (i1 == R.id.view_os_select_date) {
            selector.show();

        } else if (i1 == R.id.tv_title_right) {
            createOrder();

        } else if (i1 == R.id.view_os_add_pic) {
            if (llPicLayout.getChildCount() >= 3) {
                QDUtil.showToast(context, "最多只能选三张图片");
                return;
            }
            pickImage();

        } else if (i1 == R.id.view_os_add_file) {
            if (llFileLayout.getChildCount() >= 3) {
                QDUtil.showToast(context, "最多只能选三个文件");
                return;
            }
            pickFile();

        }
    }

    /**
     * 进入图片选择界面选择图片
     */
    private void pickImage() {
        Intent imageIntent = new Intent(context, QDSelectPhotoActivity.class);
        imageIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_SINGLE, true);
        startActivityForResult(imageIntent, REQUEST_FOR_ADD_PIC);
    }

    /**
     * 进入文件管理器选择文件
     */
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), REQUEST_FOR_ADD_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            QDUtil.showToast(context, "未找到文件管理应用，请安装文件管理应用后再试");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void showDialog(int type, ArrayList<String> strList) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("1111");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        QDOrderSelectFragment fragment = QDOrderSelectFragment.newInstance(type, strList);
        fragment.setListener(listener);
        fragment.show(getFragmentManager(), "1111");
    }

    private void createOrder() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title)) {
            QDUtil.showToast(context, "请先输入指令标题~。~");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            QDUtil.showToast(context, "请先输入指令内容~。~");
            return;
        }
        if (TextUtils.isEmpty(recvsStr)) {
            QDUtil.showToast(context, "请先选择接收人~。~");
            return;
        }
        final Map<String, String> map = new HashMap<>();
        String type = typeList.get(selectedTypeIndex).getTypeId();
        map.put("type_id", type);
        map.put("code", typeList.get(selectedTypeIndex).getTypeCode());
        map.put("level", String.valueOf(selectedLevel));
        map.put("title", QDUtil.encoderString(title));
        map.put("content", QDUtil.encoderString(content));
        map.put("recvs", recvsStr);
        map.put("notice_group", "");
        final QDOrder order = new QDOrder();
        order.setTitle(title);
        order.setContent(content);
        order.setType(type);
        order.setLevel(selectedLevel);
        order.setIsRead(QDOrder.STATUS_READ);
        order.setArchive(QDOrder.UNARCHIVE);
        order.setUserId(QDLanderInfo.getInstance().getId());
        order.setUserName(QDLanderInfo.getInstance().getName());

        picMapList.addAll(fileMapList);
        if (picMapList.size() != 0) {
            map.put("attachs", gson.toJson(picMapList));
        }
        if (!TextUtils.isEmpty(tvSelectDateInfo.getText().toString())) {
            long time = QDTimeSelector.StringToDate(tvSelectDateInfo.getText().toString(), QDTimeSelector.FORMAT.DATE_TIME.value).getTime()/1000;
            map.put("feedback_time", time + "");
            order.setFeedbackTime(time);
        } else {
            map.put("feedback_time", "");
        }

        getWarningDailog().show();
        QDOrderManager.getInstance().getOrderCode(typeList.get(selectedTypeIndex).getTypeId(), new QDResultCallBack<String>() {
            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, "发布指令失败: " + errorMsg);
            }

            @Override
            public void onSuccess(String code) {
                map.put("code", code);
                order.setOrderCode(code);
                QDOrderManager.getInstance().createOrder(map, new QDResultCallBack<JsonObject>() {
                    @Override
                    public void onError(String errorMsg) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, "发送指令失败: " + errorMsg);
                    }

                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        getWarningDailog().dismiss();
                        order.setOrderId(jsonObject.get("instr_id").getAsString());
                        order.setCreateTime(jsonObject.get("create_time").getAsLong());
                        QDOrderHelper.insertOrder(order);
                        setResult(RESULT_OK);
                        finish();
                    }

                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FOR_ADD_USER:
                    doOperateAddUser(data);
                    break;
                case REQUEST_FOR_ADD_PIC:
                    String path = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH);
                    uploadFile(new File(path), true);
                    break;
                case REQUEST_FOR_ADD_FILE:
                    String filePath = "";
                    if (Build.VERSION.SDK_INT >= 19) {
                        filePath = QDUtil.handleImageOnKitKat(context, data);
                    } else {
                        filePath = QDUtil.handleImageBeforeKitKat(context, data);
                    }
                    uploadFile(new File(filePath), false);
                    break;
            }
        }
    }

    private void uploadFile(final File file, final boolean isPic) {
        QDFileManager.getInstance().uploadFile(file, "addons", new QDFileCallBack<QDFileModel>() {
            @Override
            public void onUploading(String path, int per) {

            }

            @Override
            public void onUploadFailed(String errorMsg) {

            }

            @Override
            public void onUploadSuccess(QDFileModel model) {
                Map<String, String> map = new HashMap<>();
                map.put("id", model.getId());
                map.put("name", model.getName());
                map.put("url_original", model.getOriginal());
                map.put("url_thumbpic", model.getThumbPic());
                map.put("size", model.getSize() + "");
                if (isPic) {
                    addPic(file);
                    picMapList.add(map);
                } else {
                    addFile(file);
                    fileMapList.add(map);
                }
            }
        });
    }

    private void addPic(File file) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_order_pic, null);
        ImageView ivPic = view.findViewById(R.id.iv_item_pic);
        ImageView ivDel = view.findViewById(R.id.iv_item_del);
        ImageLoader.getInstance().displayImage("file://" + file.getPath(), ivPic);
        llPicLayout.addView(view);

        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = llPicLayout.indexOfChild(view);
                llPicLayout.removeView(view);
                picMapList.remove(index);
            }
        });
    }

    private void addFile(File file) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_order_file, null);
        TextView tvName = view.findViewById(R.id.tv_item_name);
        TextView tvSize = view.findViewById(R.id.tv_item_size);
        ImageView tvDel = view.findViewById(R.id.iv_item_del);
        tvName.setText(file.getName());
        tvSize.setText(QDUtil.changFileSizeToString(file.length()));
        llFileLayout.addView(view);

        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = llFileLayout.indexOfChild(view);
                llFileLayout.removeView(view);
                fileMapList.remove(index);
            }
        });
    }

    private void doOperateAddUser(Intent data) {

        selectedUsers = data.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
        selectedIds = data.getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST);
        List<Map<String, Object>> list = new ArrayList<>();
        for (QDUser user : selectedUsers) {
            Map<String, Object> map = new HashMap<>();
            map.put("emp_type", "u");
            map.put("emp_id", user.getId());
            map.put("emp_name", user.getName());
            list.add(map);
        }
        recvsStr = gson.toJson(list);
        tvSelectUserInfo.setText(selectedUsers.size() + "");
    }


}
