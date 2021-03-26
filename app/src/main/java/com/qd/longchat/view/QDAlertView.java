package com.qd.longchat.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;

import java.util.Arrays;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/10 下午5:32
 */

public class QDAlertView {

    private Context context;
    private Style style;
    private String title, content, warning, sureText;
    private List<String> selectList;
    private boolean isHaveCancelBtn;
    private boolean isDismissOutside;
    private boolean isAutoDismiss;

    private ViewGroup decorView;
    private RelativeLayout.LayoutParams containerParams;
    private ViewGroup container;

    private boolean isShow;

    private OnStringItemClickListener listener;

    private OnButtonClickListener clickListener;

    private OnCancelClickListener cancelClickListener;

    private LinearLayout linearLayout;

    private TextView tvContent;

    private Object userIcon;

    public enum Style {
        Alert,
        Bottom,
        Warn,
        Center,
        Send
    }

    public QDAlertView(Builder builder) {
        this.context = builder.context;
        this.style = builder.style;
        this.title = builder.title;
        this.content = builder.content;
        this.warning = builder.warning;
        this.selectList = builder.selectList;
        this.isHaveCancelBtn = builder.isHaveCancelBtn;
        this.isDismissOutside = builder.isCancelOutside;
        this.listener = builder.listener;
        this.clickListener = builder.clickListener;
        this.cancelClickListener = builder.cancelClickListener;
        this.isAutoDismiss = builder.isAutoDismiss;
        this.sureText = builder.sureText;
        this.userIcon = builder.userIcon;
        initViews();
    }

    private void initViews() {
        decorView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        container = new RelativeLayout(context);
        containerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.setBackgroundColor(context.getResources().getColor(R.color.colorAlertBg));

        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin;
        View contentView;
        if (this.style == Style.Alert) {
            contentView = getCenterView();
            contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
        } else if (this.style == Style.Bottom) {
            contentView = getBottomView();
            contentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        } else if (this.style == Style.Center) {
            contentView = getMiddleView();
            contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
        } else if (this.style == Style.Send) {
            contentView = getSendView();
            contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
        } else {
            contentView = getWarnView();
            contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
        }
        contentParams.setMargins(margin, 0, margin, 0);

        container.addView(contentView, contentParams);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDismissOutside)
                    dismiss();
            }
        });
    }

    private View getSendView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_send, decorView, false);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView cancel = view.findViewById(R.id.tv_alert_cancel);
        TextView sure = view.findViewById(R.id.tv_alert_sure);
        ImageView ivImg = view.findViewById(R.id.iv_img);

        if (userIcon instanceof Bitmap) {
            ivIcon.setImageBitmap((Bitmap) userIcon);
        } else {
            ivIcon.setImageResource((Integer) userIcon);
        }

        tvName.setText(this.title);
        if (TextUtils.isEmpty(content)) {
            tvContent.setVisibility(View.GONE);
            ivImg.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("file://" + this.warning, ivImg);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            ivImg.setVisibility(View.GONE);
            tvContent.setText(content);
        }

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(true);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(false);
                dismiss();
            }
        });
        return view;
    }


    private View getCenterView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_middle, decorView, false);
        TextView title = view.findViewById(R.id.tv_alert_title);
        tvContent = view.findViewById(R.id.tv_alert_content);
        TextView cancel = view.findViewById(R.id.tv_alert_cancel);
        TextView sure = view.findViewById(R.id.tv_alert_sure);
        ImageView divide = view.findViewById(R.id.iv_alert_divide);

        view.setBackground(context.getResources().getDrawable(R.drawable.view_round_white));
        if (TextUtils.isEmpty(this.title)) {
            title.setVisibility(View.GONE);
            tvContent.setGravity(Gravity.CENTER);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText(this.title);
            tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        tvContent.setText(this.content);

        if (!TextUtils.isEmpty(this.sureText)) {
            sure.setText(sureText);
        }

        if (isHaveCancelBtn) {
            cancel.setVisibility(View.VISIBLE);
            divide.setVisibility(View.VISIBLE);
            sure.setBackgroundResource(R.drawable.bg_alert_bottom_right);
        } else {
            cancel.setVisibility(View.GONE);
            divide.setVisibility(View.GONE);
            sure.setBackgroundResource(R.drawable.bg_alert_bottom);
        }

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(true);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(false);
                dismiss();
            }
        });
        return view;
    }

    private View getMiddleView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_bottom, decorView, false);
        TextView cancel = view.findViewById(R.id.tv_alert_shooter);
        linearLayout = view.findViewById(R.id.ll_alert_layout);
        cancel.setVisibility(View.GONE);
        updateBottomList();
        return view;
    }

    private View getBottomView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_bottom, decorView, false);
        TextView cancel = view.findViewById(R.id.tv_alert_shooter);
        linearLayout = view.findViewById(R.id.ll_alert_layout);

        updateBottomList();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private View getWarnView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_warn, null);
        TextView warn = view.findViewById(R.id.tv_alert_warn);
        warn.setText(this.warning);
        return view;
    }

    public void show() {
        if (isShow) {
            return;
        }
        decorView.addView(container, containerParams);
        Animation animation;
        if (style == Style.Alert || style == Style.Warn || style == Style.Center) {
            animation = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        } else {
            animation = AnimationUtils.loadAnimation(context, R.anim.translate_in);
        }
        container.getChildAt(0).startAnimation(animation);
        isShow = true;
    }

    public void dismiss() {
        dismiss(false);
    }

    public void dismiss(final boolean b) {
        if (isShow) {
            isShow = false;
            Animation animation;
            if (style == Style.Alert || style == Style.Warn || style == Style.Center) {
                animation = AnimationUtils.loadAnimation(context, R.anim.alpha_out);
            } else {
                animation = AnimationUtils.loadAnimation(context, R.anim.translate_out);
            }
            container.getChildAt(0).startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    decorView.removeView(container);
                    if (cancelClickListener != null) {
                        cancelClickListener.onCancel(b);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }


    public interface OnStringItemClickListener {
        void onItemClick(String str, int position);
    }

    public interface OnButtonClickListener {
        void onClick(boolean isSure);
    }

    public interface OnCancelClickListener {
        void onCancel(boolean b);
    }

    public static class Builder {
        private Context context;
        private Style style;
        private String title;
        private String content;
        private String warning;
        private Object userIcon;
        private List<String> selectList;
        private boolean isHaveCancelBtn;
        private boolean isCancelOutside;
        private OnStringItemClickListener listener;
        private OnButtonClickListener clickListener;
        private OnCancelClickListener cancelClickListener;
        private boolean isAutoDismiss = true;
        private String sureText;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setStyle(Style style) {
            this.style = style;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setSelectList(String... selectList) {
            this.selectList = Arrays.asList(selectList);
            return this;
        }

        public Builder isHaveCancelBtn(boolean b) {
            this.isHaveCancelBtn = b;
            return this;
        }

        public Builder isAutoDismiss(boolean b) {
            this.isAutoDismiss = b;
            return this;
        }

        public Builder setDismissOutside(boolean b) {
            this.isCancelOutside = b;
            return this;
        }

        public Builder setWarning(String warning) {
            this.warning = warning;
            return this;
        }

        public Builder setOnStringItemClickListener(OnStringItemClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setOnButtonClickListener(OnButtonClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public Builder setCancelClickListener(OnCancelClickListener cancelClickListener) {
            this.cancelClickListener = cancelClickListener;
            return this;
        }

        public Builder setSureText(String sureText) {
            this.sureText = sureText;
            return this;
        }

        public Builder setUserIcon(Object userIcon) {
            this.userIcon = userIcon;
            return this;
        }

        public QDAlertView build() {
            return new QDAlertView(this);
        }

    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public void setContent(String content) {
        this.tvContent.setText(content);
    }

    public void setSelectList(String... selectList) {
        this.selectList = Arrays.asList(selectList);
        updateBottomList();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void updateBottomList() {
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, com.qd.longchat.util.QDUtil.dp2px(context, 50));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        if (!TextUtils.isEmpty(title)) {
            TextView textView = new TextView(context);
            textView.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 10, 10, 10);
            params.setMargins(5,5,5,5);
            textView.setText(title);
            textView.setBackgroundColor(Color.WHITE);
            linearLayout.addView(textView, params);

            ImageView imageView = new ImageView(context);
            imageView.setBackgroundColor(context.getResources().getColor(R.color.colorLine));
            linearLayout.addView(imageView, lineParams);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        if (selectList == null) {
            return;
        }
        int size = selectList.size();
        for (int i=0; i<size; i++) {
            TextView textView = new TextView(context);
            textView.setTextColor(context.getResources().getColor(R.color.colorBtnBlue));
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 10, 10, 10);
            params.setMargins(5,5,5,5);
            textView.setText(selectList.get(i));
            textView.setBackgroundResource(R.drawable.bg_alert_test);
            linearLayout.addView(textView, params);
            final int position = i;

            if (i != size -1) {
                ImageView imageView = new ImageView(context);
                imageView.setBackgroundColor(context.getResources().getColor(R.color.colorLine));
                linearLayout.addView(imageView, lineParams);
            }

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(selectList.get(position), position);
                    }
                    if (isAutoDismiss)
                        dismiss(true);
                }
            });
        }
    }
}
