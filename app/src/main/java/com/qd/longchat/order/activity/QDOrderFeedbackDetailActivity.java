package com.qd.longchat.order.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.model.gd.QDOrderFeedbackModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/28 上午9:42
 */
public class QDOrderFeedbackDetailActivity extends QDBaseActivity {

    @BindView(R2.id.view_detail_title)
    View viewTitle;
    @BindView(R2.id.iv_detail_icon)
    ImageView ivIcon;
    @BindView(R2.id.tv_detail_name)
    TextView tvName;
    @BindView(R2.id.tv_detail_state)
    TextView tvState;
    @BindView(R2.id.tv_detail_time)
    TextView tvTime;
    @BindView(R2.id.tv_detail_content)
    TextView tvContent;
    @BindView(R2.id.tv_detail_tag)
    TextView tvTag;
    @BindView(R2.id.ll_detail_attach_layout)
    LinearLayout llAttachLayout;

    @BindString(R2.string.im_text_order_feedback_detail)
    String strTitle;

    private QDOrderFeedbackModel.ListBean bean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_fd_detail);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        bean = (QDOrderFeedbackModel.ListBean) getIntent().getSerializableExtra("bean");

        Bitmap bitmap = QDBitmapUtil.getInstance().createNameAvatar(this, bean.getCreateUid(), bean.getCreateUname());
        ivIcon.setImageBitmap(bitmap);
        tvName.setText(bean.getCreateUname());
        int state = bean.getStatus();
        if (state == 1) {
            tvState.setBackgroundResource(R.drawable.ic_order_done);
            tvState.setTextColor(Color.parseColor("#A2D8C6"));
            tvState.setText("已完成");
        } else {
            tvState.setBackgroundResource(R.drawable.ic_order_undone);
            tvState.setTextColor(Color.parseColor("#FDA6A6"));
            tvState.setText("未完成");
        }
        tvTime.setText(QDDateUtil.longToString(bean.getCreateTime() * 1000, QDDateUtil.DATE_FOR_ORDER));
        tvContent.setText(QDUtil.decodeString(bean.getContent()));
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        List<QDOrderFeedbackModel.ListBean.AttachsBean> attachList = bean.getAttachs();
        if (attachList == null || attachList.size() == 0) {
            tvTag.setVisibility(View.GONE);
        } else {
            tvTag.setVisibility(View.VISIBLE);
            tvTag.setText("附件   共" + attachList.size() + "件");
            for (QDOrderFeedbackModel.ListBean.AttachsBean attachBean : attachList) {
                addAttach(attachBean);
            }
        }
    }

    private void addAttach(final QDOrderFeedbackModel.ListBean.AttachsBean attachBean) {
        final View view = LayoutInflater.from(this).inflate(R.layout.item_order_file, null);
        TextView tvName = view.findViewById(R.id.tv_item_name);
        TextView tvSize = view.findViewById(R.id.tv_item_size);
        ImageView tvDel = view.findViewById(R.id.iv_item_del);
        tvDel.setVisibility(View.GONE);
        tvName.setText(attachBean.getName());
        tvSize.setText(QDUtil.changFileSizeToString(attachBean.getSize()));
        llAttachLayout.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = QDStorePath.APP_PATH + attachBean.getName();
                File file = new File(path);
                if (file.exists()) {
                    QDUtil.openFile(context, path);
                } else {
                    getWarningDailog().show();
                    QDFileManager.getInstance().downloadFile(path, QDUtil.replaceWebServerAndToken(attachBean.getUrlOriginal()), new QDFileDownLoadCallBack() {
                        @Override
                        public void onDownLoading(int per) {

                        }

                        @Override
                        public void onDownLoadSuccess() {
                            getWarningDailog().dismiss();
                            QDUtil.openFile(context, path);
                        }

                        @Override
                        public void onDownLoadFailed(String msg) {
                            getWarningDailog().dismiss();
                            QDUtil.showToast(context, "下载失败");
                        }
                    });
                }
            }
        });
    }
}
