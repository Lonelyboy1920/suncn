package com.qd.longchat.order.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.manager.QDOrderManager;
import com.longchat.base.model.gd.QDOrderDetailModel;
import com.longchat.base.model.gd.QDOrderPreviewModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/21 上午10:29
 */
public class QDOrderDetailActivity extends QDBaseActivity {

    public final static String ORDER_ID = "orderId";

    @BindView(R2.id.view_od_title)
    View viewTitle;
    @BindView(R2.id.tv_od_title)
    TextView tvTitle;
    @BindView(R2.id.tv_od_sender_info)
    TextView tvSenderInfo;
    @BindView(R2.id.tv_od_date)
    TextView tvDate;
    @BindView(R2.id.tv_od_count)
    TextView tvCount;
    @BindView(R2.id.tv_od_content)
    TextView tvContent;
    @BindView(R2.id.tv_od_file_tag)
    TextView tvFileTag;
    @BindView(R2.id.ll_od_attach_layout)
    LinearLayout llAttachLayout;
    @BindView(R2.id.btn_od_preview)
    Button btnPreview;

    @BindString(R2.string.im_text_order_detail)
    String strTitle;

    private String orderId;
    private Context context;
    private QDOrderDetailModel detailModel;
    private QDOrderPreviewModel previewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        context = this;
        initTitleView(viewTitle);
        tvTitle.setText(strTitle);

        orderId = getIntent().getStringExtra(ORDER_ID);
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        getOrderDetail();
    }

    private void getOrderDetail() {
        getWarningDailog().setTip("正在获取指令详情，请稍后...");
        QDOrderManager.getInstance().getOrderDetail(orderId, new QDResultCallBack<QDOrderDetailModel>() {

            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, errorMsg);
            }

            @Override
            public void onSuccess(QDOrderDetailModel model) {
                detailModel = model;
                getPreviewList();
            }

        });
    }

    private void getPreviewList() {
        QDOrderManager.getInstance().getPreviewList(orderId, new QDResultCallBack<QDOrderPreviewModel>() {

            @Override
            public void onSuccess(QDOrderPreviewModel model) {
                getWarningDailog().dismiss();
                previewModel = model;
                initUI();
            }

            @Override
            public void onError(String msg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, msg);
            }
        });
    }

    private void initUI() {
        String title = QDUtil.decodeString(detailModel.getTitle());
        String content = QDUtil.decodeString(detailModel.getSubject());
        String createTime = QDDateUtil.longToString(detailModel.getCreateTime() * 1000, QDDateUtil.DATE_FOR_MEETING);
        long feedbackTime = detailModel.getFeedbackTime();
        tvTitle.setText(title);
        tvSenderInfo.setText(detailModel.getUpdateTime() + " 发布于 " + createTime);
        if (feedbackTime == 0) {
            tvDate.setVisibility(View.INVISIBLE);
        } else {
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText("该条指令要求在 " + QDDateUtil.longToString(feedbackTime * 1000, QDDateUtil.DATE_FOR_MEETING) + "前提交反馈内容");
        }
        int read = previewModel.getRead().getCount();
        int unread = previewModel.getUnread().getCount();
        tvCount.setText("已读: " + read + "/" + (read + unread));
        tvContent.setText(content);

        List<QDOrderDetailModel.AttachsBean> attachList = detailModel.getAttachs();
        if (attachList == null || attachList.size() == 0) {
            tvFileTag.setVisibility(View.GONE);
        } else {
            tvFileTag.setText("附件   共" + attachList.size() + "件");
            for (QDOrderDetailModel.AttachsBean attach : attachList) {
                addViewToLayout(attach);
            }

        }
    }

    private void addViewToLayout(final QDOrderDetailModel.AttachsBean attachBean) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_order_file, null);
        TextView tvName = view.findViewById(R.id.tv_item_name);
        TextView tvSize = view.findViewById(R.id.tv_item_size);
        ImageView tvDel = view.findViewById(R.id.iv_item_del);
        tvDel.setVisibility(View.GONE);
        tvName.setText(attachBean.getName());
        tvSize.setText(QDUtil.changFileSizeToString(attachBean.getSize()));
        llAttachLayout.addView(view);
        final String fileName = attachBean.getName();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = QDStorePath.APP_PATH + fileName;
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

    @OnClick({R2.id.tv_od_count, R2.id.btn_od_preview})
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.tv_od_count) {
            Intent intent = new Intent(context, QDOrderPreviewDetailActivity.class);
            intent.putExtra("detail", previewModel);
            startActivity(intent);

        } else if (i1 == R.id.btn_od_preview) {
            String createUserId = detailModel.getCreateUid();
            Intent i = new Intent(context, QDOrderFeedbackListActivity.class);
            i.putExtra("orderId", orderId);
            i.putExtra("createUserId", createUserId);
            startActivity(i);

        }
    }

}
