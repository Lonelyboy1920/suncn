package com.qd.longchat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;

import com.gavin.giframe.utils.GIToastUtil;
import com.qd.longchat.R;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.qd.longchat.view.QDXEditText;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.qd.longchat.R2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/6 上午10:15
 */
public class QDModifyInfoActivity extends QDBaseActivity {

    @BindView(R2.id.view_mi_title)
    View viewTitle;
    @BindView(R2.id.et_mi_input)
    QDXEditText etInput;

    @BindString(R2.string.title_save)
    String strSave;
    @BindString(R2.string.modify_info_empty_input)
    String strEmpty;
    @BindString(R2.string.modify_info_email_not_match)
    String strEmailNotMatch;
    @BindString(R2.string.modify_info_mobile_not_match)
    String strMobileNotMatch;
    @BindString(R2.string.modify_info_modify)
    String strModify;

    @BindString(R2.string.self_email)
    String strEmail;
    @BindString(R2.string.self_mobile)
    String strMobile;
    @BindString(R2.string.self_ophone)
    String strOphone;
    @BindString(R2.string.modify_info_not_rename)
    String strNotRename;
    @BindString(R2.string.modify_info_modify_suffix)
    String strModifySuffix;
    @BindString(R2.string.modify_info_modify_title)
    String strModifyTitle;
    @BindView(R2.id.view_place)
    View view_place;
    private String strTitle;
    private String strRight;
    private boolean isFile;
    private int index;
    private String ext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_modify_info);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        view_place.setVisibility(View.VISIBLE);
        strTitle = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME);
        strRight = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_RIGHT_NAME);
        boolean isLimit = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_SELECT_LIMIT, true);
        if (isLimit) {
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
            etInput.setFilters(filters);
        }
        if (TextUtils.isEmpty(strRight)) {
            tvTitleRight.setText(strSave);
        } else {
            tvTitleRight.setText(strRight);
        }
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        String text = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO);
        isFile = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_FILE, false);
        if (isFile) {
            if (text.contains(".")) {
                index = text.lastIndexOf(".");
                ext = text.substring(index);
                SpannableString spannableString = new SpannableString(text);
                BackgroundColorSpan colorSpan = new BackgroundColorSpan(Color.parseColor("#AC00FF30"));
                spannableString.setSpan(colorSpan, 0, index, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                etInput.setText(spannableString);
                etInput.setSelection(index);
                QDUtil.openKeybord(etInput, context);
            } else {
                etInput.setText(text);
                etInput.setSelection(etInput.length());
            }
        } else {
            etInput.setText(text);
            etInput.setSelection(etInput.length());
        }
    }

//    @OnTextChanged(R2.id.et_mi_input)
//    public void onTextChanged(CharSequence text, int start, int before, int count) {
//        if (isFile) {
//            BackgroundColorSpan[] spans = etInput.getText().getSpans(0, etInput.length(), BackgroundColorSpan.class);
//            for (BackgroundColorSpan span : spans) {
//                int end = etInput.getText().getSpanEnd(span);
//                if (end == start) {
//                    int spanStart = etInput.getText().getSpanStart(span);
//                    etInput.getText().delete(spanStart, end);
//                }
//            }
//        }
//    }

    @OnClick({R2.id.tv_title_right, R2.id.et_mi_input})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_title_right) {
            String strInput = etInput.getText().toString();
            if (TextUtils.isEmpty(strInput) && !strTitle.equals("群描述")) {
                QDUtil.showToast(context, strEmpty);
                return;
            }
            if (strTitle.equals(strEmail) && !QDUtil.isMatchEmail(strInput)) {
                QDUtil.showToast(context, strEmailNotMatch);
                return;
            }
            if (strTitle.equals(strMobile) && !QDUtil.isMatchMobile(strInput)) {
                QDUtil.showToast(context, strMobileNotMatch);
                return;
            }

            if (strInput.equals(getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO))) {
                QDUtil.showToast(context, strModify);
                return;
            }

            if (isFile && ext.equalsIgnoreCase(strInput)) {
                QDUtil.showToast(context, strNotRename);
                return;
            }

            if (isFile && !strInput.endsWith(ext)) {
                QDUtil.closeKeybord(etInput, context);
                showDialog();
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, strInput);
            setResult(RESULT_OK, intent);
            finish();
        } else if (view.getId() == R.id.et_mi_input) {
            if (isFile) {
                BackgroundColorSpan[] spans = etInput.getText().getSpans(0, etInput.length(), BackgroundColorSpan.class);
                int selection = etInput.getSelectionStart();
                if (spans.length != 0 && selection != index) {
                    etInput.setText(etInput.getText().toString());
                    if (selection > etInput.length()) {
                        etInput.setSelection(etInput.length());
                    } else {
                        etInput.setSelection(selection);
                    }
                }
            }

        }
    }

    private void showDialog() {
        QDAlertView view = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Alert)
                .setTitle(strModifyTitle)
                .setContent(strModifySuffix)
                .isHaveCancelBtn(true)
                .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                    @Override
                    public void onClick(boolean isSure) {
                        if (isSure) {
                            Intent intent = new Intent();
                            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, etInput.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }).build();
        view.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        QDUtil.closeKeybord(etInput, context);
    }
}
