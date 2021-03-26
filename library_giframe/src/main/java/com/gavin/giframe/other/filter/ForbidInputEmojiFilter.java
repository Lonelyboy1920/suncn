package com.gavin.giframe.other.filter;

import android.text.InputFilter;
import android.text.Spanned;

import com.gavin.giframe.utils.GIToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 限制不让输入表情的过滤器
 */
public class ForbidInputEmojiFilter implements InputFilter {
    private Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_/^[\\u4e00-\\u9fa5_a-zA-Z0-9\\s\\·\\~\\！\\@\\#\\￥\\%\\……\\&\\*\\（\\）\\——\\-\\+\\=\\【\\】\\{\\}\\、\\|\\；\\‘\\’\\：\\“\\”\\《\\》\\？\\，\\。\\、\\`\\~\\!\\#\\$\\%\\^\\&\\*\\(\\)\\_\\[\\]{\\}\\\\\\|\\;\\'\\'\\:\\\"\\\"\\,\\.\\/\\<\\>\\?]+$/]");

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        Matcher matcher = pattern.matcher(charSequence);
        if (!matcher.find()) {
            return null;
        } else {
            GIToastUtil.showMessage("禁止输入表情");
            return "";
        }
    }
}