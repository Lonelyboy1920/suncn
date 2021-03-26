package com.qd.longchat.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.qd.longchat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QDChatSmiley {

    public final int[] mSmileIDs = {R.mipmap.im_smiley_0, R.mipmap.im_smiley_1, R.mipmap.im_smiley_2, R.mipmap.im_smiley_3,
            R.mipmap.im_smiley_4, R.mipmap.im_smiley_5, R.mipmap.im_smiley_6, R.mipmap.im_smiley_7, R.mipmap.im_smiley_8,
            R.mipmap.im_smiley_9, R.mipmap.im_smiley_10, R.mipmap.im_smiley_11, R.mipmap.im_smiley_12, R.mipmap.im_smiley_13,
            R.mipmap.im_smiley_14, R.mipmap.im_smiley_15, R.mipmap.im_smiley_16, R.mipmap.im_smiley_17, R.mipmap.im_smiley_18,
            R.mipmap.im_smiley_19, R.mipmap.im_smiley_20, R.mipmap.im_smiley_21, R.mipmap.im_smiley_22, R.mipmap.im_smiley_23,
            R.mipmap.im_smiley_24, R.mipmap.im_smiley_25, R.mipmap.im_smiley_26, R.mipmap.im_smiley_27, R.mipmap.im_smiley_28,
            R.mipmap.im_smiley_29, R.mipmap.im_smiley_30, R.mipmap.im_smiley_31, R.mipmap.im_smiley_32, R.mipmap.im_smiley_33,
            R.mipmap.im_smiley_34, R.mipmap.im_smiley_35, R.mipmap.im_smiley_36, R.mipmap.im_smiley_37, R.mipmap.im_smiley_38,
            R.mipmap.im_smiley_39, R.mipmap.im_smiley_40, R.mipmap.im_smiley_41, R.mipmap.im_smiley_42, R.mipmap.im_smiley_43,
            R.mipmap.im_smiley_44, R.mipmap.im_smiley_45, R.mipmap.im_smiley_46, R.mipmap.im_smiley_47, R.mipmap.im_smiley_48,
            R.mipmap.im_smiley_49, R.mipmap.im_smiley_50, R.mipmap.im_smiley_51, R.mipmap.im_smiley_52, R.mipmap.im_smiley_53,
            R.mipmap.im_smiley_54, R.mipmap.im_smiley_55, R.mipmap.im_smiley_56, R.mipmap.im_smiley_57};
    public static final int DEFAULT_SMILEY_TEXTS = R.array.smiley_array;

    private final Context context;
    private final String[] smileyTexts;
    private final Pattern pattern;
    private final HashMap<String, Drawable> smileyToRes;
    private List<Drawable> smileyList;

    private static QDChatSmiley instance;

    public QDChatSmiley(Context context) {
        this.context = context;
        smileyTexts = this.context.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
        initSmileyImage();
        smileyToRes = buildSmileyToRes();
        pattern = buildPattern();
        instance = this;
    }

    public static QDChatSmiley getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        }
        return instance;
    }


    public static QDChatSmiley getInstance(Context context) {
        if (instance == null) {
            instance = new QDChatSmiley(context);
        }
        return instance;
    }

    public List<Drawable> getSmileyList() {
        return smileyList;
    }

    public Drawable getSmileyImage(int index) {
        if (index >= 0 && index < getSmileyCount()) {
            return smileyList.get(index);
        }
        return null;
    }

    private void initSmileyImage() {
        smileyList = new ArrayList<>();

        int count = mSmileIDs.length;
        for (int i = 0; i < count; i++) {
            int id = mSmileIDs[i];
            BitmapDrawable bmp = (BitmapDrawable) ContextCompat.getDrawable(context, id);
            smileyList.add(bmp.mutate());
        }
    }

    public int getSmileyCount() {
        return smileyList.size();
    }

    private HashMap<String, Drawable> buildSmileyToRes() {

        HashMap<String, Drawable> smileyToRes = new HashMap<>(smileyTexts.length);

        for (int i = 0; i < smileyTexts.length; i++) {
            smileyToRes.put(smileyTexts[i], smileyList.get(i));
        }

        return smileyToRes;
    }

    private Pattern buildPattern() {
        StringBuilder patternString = new StringBuilder(smileyTexts.length * 3);
        patternString.append('(');
        for (String s : smileyTexts) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        patternString.replace(patternString.length() - 1, patternString.length(), ")");
        return Pattern.compile(patternString.toString());
    }

    public CharSequence getSmileyByIndex(int index) {
        String smiley = smileyTexts[index];
        return strToSmiley(smiley);
    }

    public CharSequence strToSmiley(CharSequence text) {

        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            Drawable drawable = smileyToRes.get(matcher.group());
            drawable.setBounds(0, 0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
            ImageSpan imageSpan = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
            builder.setSpan(imageSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    public String strToSmileyInfo(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            text = text.replace(matcher.group(), "[表情]");
        }
        return text;

    }
}