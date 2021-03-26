package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.utils.GIToastUtil;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.util.QDIntentKeyUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/26 下午5:59
 */
public class QDGroupNoticeActivity extends QDBaseActivity {

    @BindView(R2.id.view_notice_title)
    View viewTitle;
    @BindView(R2.id.et_notice_edit)
    EditText etNotice;
    @BindView(R2.id.tv_notify_content)
    TextView tvContent;
    @BindView(R2.id.tv_notify_edit)
    TextView tvNotice;
    @BindView(R2.id.iv_notice_line)
    ImageView ivLine;

    @BindString(R2.string.group_notice_empty)
    String strEmpty;
    @BindString(R2.string.group_notice_issue)
    String strIssue;
    @BindString(R2.string.group_info_notice)
    String strTitle;
    @BindView(R2.id.view_place)
    View viewPlace;

    private boolean isEdit;
    private String notice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_group_notice);
        ButterKnife.bind(this);
        viewPlace.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        tvTitleRight.setText(strIssue);

        isEdit = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_EDIT, false);
        notice = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_NOTICE);
        InputFilter[] filters = {new InputFilter() {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find()) {
                    GIToastUtil.showMessage(activity, "不支持输入表情");
                    return "";
                }
                return null;
            }
        }};
        etNotice.setFilters(filters);

        if (isEdit) {
            if (TextUtils.isEmpty(notice)) {
                etNotice.setVisibility(View.VISIBLE);
                ivLine.setVisibility(View.GONE);
                tvNotice.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                tvTitleRight.setVisibility(View.VISIBLE);
            } else {
                etNotice.setVisibility(View.GONE);
                ivLine.setVisibility(View.VISIBLE);
                tvNotice.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.VISIBLE);
                tvTitleRight.setVisibility(View.GONE);
                tvContent.setText(notice);
                etNotice.setText(notice);
            }
        } else {
            etNotice.setVisibility(View.GONE);
            ivLine.setVisibility(View.GONE);
            tvNotice.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            tvTitleRight.setVisibility(View.GONE);
            if (TextUtils.isEmpty(notice)) {
                tvContent.setText(strEmpty);
            } else {
                tvContent.setText(notice);
            }
        }
    }

    @OnClick({R2.id.tv_notify_edit, R2.id.tv_title_right})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_notify_edit) {
            etNotice.setVisibility(View.VISIBLE);
            ivLine.setVisibility(View.GONE);
            tvNotice.setVisibility(View.GONE);
            tvContent.setVisibility(View.GONE);
            tvTitleRight.setVisibility(View.VISIBLE);

        } else if (i == R.id.tv_title_right) {
            String notice = etNotice.getText().toString();
            if (TextUtils.isEmpty(notice.trim())) {
                GIToastUtil.showMessage("请输入群公告");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_NOTICE, notice);
            setResult(RESULT_OK, intent);
            finish();

        }
    }

}
