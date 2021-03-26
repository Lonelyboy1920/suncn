package com.qd.longchat.order.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.manager.QDOrderManager;
import com.longchat.base.model.gd.QDFileModel;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/27 下午2:47
 */
public class QDOrderFeedbackActivity extends QDBaseActivity {

    private final static int REQUEST_FOR_ADD_FILE = 101;

    @BindView(R2.id.view_fd_title)
    View viewTitle;
    @BindView(R2.id.tv_fd_done)
    TextView tvDone;
    @BindView(R2.id.tv_fd_undone)
    TextView tvUndone;
    @BindView(R2.id.et_fd_edit)
    EditText etEdit;
    @BindView(R2.id.tv_fd_tag)
    TextView tvTag;
    @BindView(R2.id.ll_fd_attach_layout)
    LinearLayout llAttachLayout;
    @BindView(R2.id.btn_fd_feedback)
    Button btnFeedback;

    @BindString(R2.string.im_text_order_feedback_detail)
    String strTitle;

    private Context context;
    private int doneIndex = -1;
    private String orderId;
    private List<Map<String, String>> fileMapList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_feedback);
        ButterKnife.bind(this);
        context = this;
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        fileMapList = new ArrayList<>();
        orderId = getIntent().getStringExtra("orderId");
    }

    @OnClick({R2.id.tv_fd_done, R2.id.tv_fd_undone, R2.id.tv_fd_tag, R2.id.btn_fd_feedback})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_fd_done) {
            if (doneIndex == 1) {
                return;
            }
            initTextDrawable();
            doneIndex = 1;
            tvDone.setCompoundDrawables(QDUtil.getBoundedDrawable(context, R.drawable.ic_round_selected), null, null, null);

        } else if (i == R.id.tv_fd_undone) {
            if (doneIndex == 0) {
                return;
            }
            initTextDrawable();
            doneIndex = 0;
            tvUndone.setCompoundDrawables(QDUtil.getBoundedDrawable(context, R.drawable.ic_round_selected), null, null, null);

        } else if (i == R.id.tv_fd_tag) {
            if (llAttachLayout.getChildCount() >= 3) {
                QDUtil.showToast(context, "最多只能选三个附件");
                return;
            }
            pickFile();

        } else if (i == R2.id.btn_fd_feedback) {
            createFeedback();

        }
    }

    private void initTextDrawable() {
        Drawable drawable = QDUtil.getBoundedDrawable(context, R.drawable.ic_round_unselected);
        tvDone.setCompoundDrawables(drawable, null, null, null);
        tvUndone.setCompoundDrawables(drawable, null, null, null);
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
            QDUtil.showToast(this, "未找到文件管理应用，请安装文件管理应用后再试");
        }
    }

    private void createFeedback() {
        String content = etEdit.getText().toString();
        if (doneIndex == -1) {
            QDUtil.showToast(context, "请先选择完成或者未完成~。~");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            QDUtil.showToast(context, "请先填写反馈内容~。~");
            return;
        }

        getWarningDailog().show();
        Map<String, String> map = new HashMap<>();
        map.put("content", QDUtil.encoderString(content));
        map.put("status", String.valueOf(doneIndex));
        if (fileMapList.size() != 0) {
            map.put("attachs", QDGson.getGson().toJson(fileMapList));
        }
        QDOrderManager.getInstance().createFeedback(orderId, map, new QDResultCallBack() {
            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, "反馈失败：" + errorMsg);
            }

            @Override
            public void onSuccess(Object o) {
                getWarningDailog().dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOR_ADD_FILE) {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= 19) {
                filePath = QDUtil.handleImageOnKitKat(context, data);
            } else {
                filePath = QDUtil.handleImageBeforeKitKat(context, data);
            }
            uploadFile(new File(filePath));
        }
    }

    private void uploadFile(final File file) {
        getWarningDailog().show();
        QDFileManager.getInstance().uploadFile(file, "addons", new QDFileCallBack<QDFileModel>() {

                    @Override
                    public void onUploading(String path, int per) {

                    }

                    @Override
                    public void onUploadFailed(String errorMsg) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, "上传失败，请检查网络");
                    }

                    @Override
                    public void onUploadSuccess(QDFileModel model) {
                        getWarningDailog().dismiss();
                        Map<String, String> map = new HashMap<>();
                        map.put("id", model.getId());
                        map.put("name", model.getName());
                        map.put("url_original", model.getOriginal());
                        map.put("url_thumbpic", model.getThumbPic());
                        map.put("size", model.getSize() + "");
                        fileMapList.add(map);
                        addFile(file);
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
        llAttachLayout.addView(view);

        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = llAttachLayout.indexOfChild(view);
                llAttachLayout.removeView(view);
                fileMapList.remove(index);
            }
        });
    }
}
