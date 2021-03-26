package com.suncn.ihold_zxztc.view;

import android.Manifest;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.gavin.giframe.utils.GIToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.utils.XfJsonParserUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;


public class ClearEditText extends androidx.appcompat.widget.AppCompatEditText implements OnFocusChangeListener, TextWatcher{
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    public boolean hasFoucs;

    private boolean isVisibileMike; // 是否显示话筒

    private boolean isHaveText; // 输入框是否有内容
    private Context context;

    public boolean isSearch; // 是否是查询操作
    private OptionSearch mOptionSearch; // 搜索工具类

    private RecognizerDialog mIatDialog; // 语音听写UI
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();  // 用HashMap存储听写结果
    private SpeechRecognizer mIat;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer,使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(context, mInitListener);
        //mIatDialog.setListener(mRecognizerDialogListener);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
        isVisibileMike = ta.getBoolean(R.styleable.ClearEditText_isVisibleMike, false);
       /* if (isVisibileMike) {
            mIat.getRecognizer().setParameter(SpeechConstant.ASR_PTT, "0");
        }*/

        init();
    }
    public OptionSearch getmOptionSearch(boolean isSearch) {
        this.isSearch = isSearch;
        return mOptionSearch = new OptionSearch(Looper.myLooper());
    }

    public void setSearch(boolean search) {
        isSearch = search;
    }
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                GIToastUtil.showMessage(context, "初始化失败");
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, isLast);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {

        }
    };


    /**
     * 识别结果
     */
    private void printResult(RecognizerResult results, boolean isTrue) {
        String opinion = getText().toString();
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
      /*  for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }*/
        for (Map.Entry key : mIatResults.entrySet()) {
            resultBuffer.append(key.getValue());
        }
        if (isTrue) {
            this.requestFocus();
            this.setText(opinion + resultBuffer.toString());
            this.setSelection(this.length());
        }
    }


    private void init() {
        mOptionSearch = new OptionSearch(Looper.myLooper());

        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            // throw new
            // NullPointerException("You can add drawableRight attribute in XML");
            if (isVisibileMike) {
                // mClearDrawable = getResources().getDrawable(R.mipmap.ic_mike);
                mClearDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_mike);
            } else {
                //mClearDrawable = getResources().getDrawable(R.mipmap.btn_clear_edit);
                mClearDrawable = ContextCompat.getDrawable(context, R.mipmap.btn_clear_edit);
            }
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        // 默认设置隐藏图标
        if (isVisibileMike) {
            setClearIconVisible(true);
        } else {
            setClearIconVisible(false);
        }

        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
//        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 - 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    if ((!isVisibileMike) || (isHaveText && isVisibileMike)) {
                        this.setText("");
                    } else {
                        //请求授权
                        HiPermission.create(context).checkSinglePermission(Manifest.permission.RECORD_AUDIO, new PermissionCallback() {
                            @Override
                            public void onGuarantee(String permisson, int position) { // 同意/已授权
                                mIatResults.clear();
                                // 设置参数
                                Utils.setMikeParam(mIat, isVisibileMike);
                                mIatDialog.setListener(mRecognizerDialogListener);
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
                }
            }
        }

        return super.onTouchEvent(event);
    }


    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setDrawRightIconVisible();
        } else {
            if (isVisibileMike) {
                // mClearDrawable = getResources().getDrawable(R.mipmap.ic_mike);
                mClearDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_mike);
                mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
                setClearIconVisible(true);
            } else {
                setClearIconVisible(false);
            }
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    public void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setDrawRightIconVisible();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isSearch) {
            mOptionSearch.optionSearch(s.toString().trim()); // 把每次输入框改变的字符串传给一个工具类，并让它来判断是否进行搜索
        }
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    private void setDrawRightIconVisible() {
        isHaveText = getText().length() > 0;
        if (!isVisibileMike) {
            setClearIconVisible(isHaveText);
        } else {
            if (isHaveText) {
                // mClearDrawable = getResources().getDrawable(R.mipmap.btn_clear_edit);
                mClearDrawable = ContextCompat.getDrawable(context, R.mipmap.btn_clear_edit);
                mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
            } else {
                //mClearDrawable = getResources().getDrawable(R.mipmap.ic_mike);
                mClearDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_mike);
                mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
            }
            setClearIconVisible(true);
        }
    }
}
