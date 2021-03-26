package com.gavin.giframe.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class GIStringUtil {
    /**
     * 半角转全角
     */
    public static String halfToFull(String half) {
        if (isEmpty(half))
            return half;
        char c[] = half.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     */
    public static String fullToHalf(String full) {
        if (isEmpty(full))
            return full;
        char c[] = full.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 处理html中的特殊字符串
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     */
    public static String htmlEscapeCharsToString(String html) {
        return isEmpty(html) ? html : html.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"")
                .replaceAll("<br>", "\n").replaceAll("＼n", "\n").replace("\\n", "\n");
    }

    /**
     * 将字符串用UTF-8编码，发生异常时，抛出异常
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     */
    public static String utf8UrlEncode(String str) {
        return urlEncode(str, "UTF-8");
    }

    /**
     * 将字符串用指定的编码进行编码，发生异常时，抛出异常
     */
    public static String urlEncode(String str, String charset) {
        if (!isEmpty(str)) {
            try {
                return URLEncoder.encode(str, charset);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
                throw new RuntimeException("UnsupportedEncodingException occurred. ", ex);
            }
        }
        return str;
    }

    /**
     * 字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * 字符串是否为非空
     */
    public static boolean isNotEmpty(String str) {
        return (str != null && str.length() != 0);
    }

    /**
     * 字符串是否为空格串
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * 字符串是否非空格串
     */
    public static boolean isNotBlank(String str) {
        return (str != null && str.trim().length() != 0);
    }

    /**
     * 将null转换为空串,如果参数为非null，则直接返回
     */
    public static String nullToEmpty(String str) {
        return (str == null ? "" : str);
    }

    /**
     * 判断字符串最后一个字符是否是“,”
     */
    public static boolean isLastDot(String str) {
        return str.lastIndexOf(",") == str.length() - 1;
    }

    /**
     * 删除最后一个“,”
     */
    public static String catLastDot(String str) {
        if (GIStringUtil.isNotBlank(str) && isLastDot(str)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 验证手机号格式是否正确
     */
    public static boolean isMobile(String mobile) {
       /* Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();*/
        return isNotBlank(mobile) && mobile.length() == 11;

    }

    /**
     * 验证email格式是否正确
     */
    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 验证是否是有效密码
     */
    public static boolean isValidPassword(String str) {
        // 长度不少于8位且必须由大小写英文字母、数字和特殊字符中两者以上的结合（二级等保要求）
        Pattern p = Pattern.compile("^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 验证是否是有效域名
     */
    public static boolean isValidDomain(String str) {
        Pattern p = Pattern.compile("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 验证是否是15位的身份证号
     */
    public static boolean isIDCard15(final CharSequence input) {
        return isMatch("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$", input);
    }

    /**
     * 验证是否是18位的身份证号
     */
    public static boolean isIDCard18(final CharSequence input) {
        return isMatch("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$", input);
    }

    /**
     * 验证是否是URL链接
     */
    public static boolean isURL(final CharSequence input) {
        return isMatch("[a-zA-z]+://[^\\s]*", input);
    }

    /**
     * 验证是否是中文
     */
    public static boolean isZh(final CharSequence input) {
        return isMatch("^[\\u4e00-\\u9fa5]+$", input);
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     */
    public static boolean isNumeric(String str) {
        return isMatch("[0-9]*", str);
    }

    private static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    /**
     * 判断json格式是否正确（无法判断，无效）
     */
    public static boolean isValidJson(String json) {
        if (GIStringUtil.isBlank(json)) {
            return false;
        }
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(json);
            return jsonElement.isJsonObject();
        } catch (JsonParseException e) {
            return false;
        }
    }
}
