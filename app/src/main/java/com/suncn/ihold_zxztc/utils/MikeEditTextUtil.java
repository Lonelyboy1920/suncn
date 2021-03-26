package com.suncn.ihold_zxztc.utils;

import android.Manifest;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gavin.giframe.utils.GIToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.suncn.ihold_zxztc.view.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * 语音识别工具类
 */
public class MikeEditTextUtil {
    private Context mContext;
    private View view;
    private RecognizerDialog mIatDialog; // 语音听写UI
    private HashMap<String, String> mIatResults = new LinkedHashMap<>(); // 用HashMap存储听写结果

    public MikeEditTextUtil(Context mContext) {
        this.mContext = mContext;
        init();
    }

    public MikeEditTextUtil(Context mContext, View view) {
        this.mContext = mContext;
        this.view = view;
        init();
    }

    private void init() {
        mIatDialog = new RecognizerDialog(mContext, new InitListener() { // 初始化监听器
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    GIToastUtil.showMessage(mContext, "初始化失败");
                }
            }
        });
        mIatDialog.setListener(mRecognizerDialogListener);
        SpeechRecognizer.getRecognizer().setParameter(SpeechConstant.ASR_PTT, "1");
    }

    public void viewShow() {
        //init();
        //请求授权
        HiPermission.create(mContext).checkSinglePermission(Manifest.permission.RECORD_AUDIO, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // 同意/已授权
                mIatResults.clear();
                mIatDialog.show();
                //获取字体所在的控件，设置为"",隐藏字体，
                TextView txt = mIatDialog.getWindow().getDecorView().findViewWithTag("textlink");
                txt.setText("");
            }

            @Override
            public void onClose() { // 用户关闭权限申请

            }

            @Override
            public void onFinish() { // 所有权限申请完成

            }

            @Override
            public void onDeny(String permisson, int position) { // 拒绝

            }
        });
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) { // 识别结果
            String value = "";
            int index = 0;
            if (view instanceof ClearEditText) {
                value = ((ClearEditText) view).getText().toString();
                index = ((ClearEditText) view).getSelectionStart();//光标位置，插入语音输入
            } else if (view instanceof EditText) {
                value = ((EditText) view).getText().toString();
                index = ((EditText) view).getSelectionStart();//光标位置，插入语音输入
            }
            //int index = ((ClearEditText) view).getSelectionStart();//光标位置，插入语音输入
            String text = XfJsonParserUtil.parseIatResult(results.getResultString());
            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIatResults.put(sn, text);
            StringBuilder resultBuffer = new StringBuilder();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            if (isLast) {
                if (view instanceof ClearEditText) {
                    view.requestFocus();
                    ((ClearEditText) view).setText(value.substring(0, index) + resultBuffer.toString() + value.substring(index, value.length()));//截取的方式
                    //editText.getText().insert(index, resultBuffer.toString());//插入的方式
                    ((ClearEditText) view).setSelection(index + resultBuffer.toString().length());//光标位置
                } else if (view instanceof EditText) {
                    view.requestFocus();
                    ((EditText) view).setText(value.substring(0, index) + resultBuffer.toString() + value.substring(index, value.length()));//截取的方式
                    //editText.getText().insert(index, resultBuffer.toString());//插入的方式
                    ((EditText) view).setSelection(index + resultBuffer.toString().length());//光标位置
                }
                /*content_EditText.setText(value + resultBuffer.toString());
                content_EditText.setSelection(content_EditText.length());*/
            }
        }

        public void onError(SpeechError error) { // 识别回调错误.
        }
    };
}
