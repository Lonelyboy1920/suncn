package com.qd.longchat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.qd.longchat.R;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/5 下午2:20
 */
public class QDModifyNoteActivity extends QDBaseActivity {

    @BindView(R2.id.view_mn_title)
    View viewTitle;
    @BindView(R2.id.et_mn_input)
    EditText etNote;
    @BindView(R2.id.tv_mn_stat)
    TextView tvStat;

    @BindString(R2.string.title_save)
    String strSave;
    @BindString(R2.string.note_success)
    String strSuccess;
    @BindString(R2.string.note_failed)
    String strFailed;
    @BindString(R2.string.input_length_too_longer)
    String strTooLonger;

    private String strTitle;
    private String strRight;
    private boolean isChange;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isChange = true;
            tvTitleRight.setTextColor(Color.WHITE);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 50) {
                etNote.removeTextChangedListener(textWatcher);
                String text = s.toString().substring(0, 50);
                etNote.setText(text);
                etNote.setSelection(text.length());
                tvStat.setText(text.length() + "/50");
                HideSoftInput();
                QDUtil.showToast(context, strTooLonger);
                etNote.addTextChangedListener(textWatcher);
            } else {
                tvStat.setText(s.length() + "/50");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_note);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        strTitle = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME);
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText(strSave);
        isChange = false;
        String text = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO);
        etNote.setText(text);
        etNote.setSelection(etNote.length());
        tvStat.setText(etNote.length() + "/50");
        etNote.addTextChangedListener(textWatcher);
        if (isChange) {
            tvTitleRight.setTextColor(Color.WHITE);
        } else {
            tvTitleRight.setTextColor(Color.parseColor("#5b7e96"));
        }
    }

    @OnClick(R2.id.tv_title_right)
    public void onClick() {
        if (isChange) {
            final String info = etNote.getText().toString();
            Map<String, String> map = new HashMap<>();
            map.put("intro", info);
            QDClient.getInstance().updateSelfInfo(map, new QDResultCallBack() {
                @Override
                public void onError(String errorMsg) {
                    QDUtil.showToast(context, strFailed);
                }

                @Override
                public void onSuccess(Object o) {
                    QDUtil.showToast(context, strSuccess);
                    Intent intent = new Intent();
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, info);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }


}
