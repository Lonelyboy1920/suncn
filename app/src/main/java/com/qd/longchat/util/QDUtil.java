package com.qd.longchat.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuItem;

import com.longchat.base.QDClient;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDFriendHelper;
import com.longchat.base.model.QDLoginParams;
import com.longchat.base.util.QDConst;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDFileDisplayActivity;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.activity.QDPlayMusicActivity;
import com.qd.longchat.activity.QDPlayerVideoActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.view.QDAlertView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/18 ??????10:43
 */

public class QDUtil {

    private static final String DRAFT_FILE = "draft.txt";

    public static String replaceWebServerAndToken(String url) {
        String webServer = getWebApiServer();
        return url.replace("[webserver]", webServer);
//                .replace("[token]", QDSDKConfig.getInstance().getWebToken());
    }

    public static String relaceAVServer(String url) {
        return url.replace("[meida_server]", QDLanderInfo.getInstance().getMediaServer()).replace("[uname]", QDLanderInfo.getInstance().getName());
    }

    public static String replaceWebUrl(String url) {
        if (QDClient.getInstance().getLoginInfo() == null) {
            return url;
        }
        String ssid = QDClient.getInstance().getLoginInfo().getSSID();
        String uid = QDClient.getInstance().getLoginInfo().getUserID();
        String username = QDClient.getInstance().getLoginInfo().getUserName();
        String loginName = QDClient.getInstance().getLoginInfo().getAccount();
        String verifytoken = QDClient.getInstance().getLoginInfo().getConfig("verifytoken");
        String pwd = QDLanderInfo.getInstance().getPassword();
        String password = "";
        String pw5 = "";
        if (!TextUtils.isEmpty(pwd)) {
            password = decodeString(pwd);
            pw5 = com.longchat.base.util.QDUtil.getMd5(password);
        }
        return url.replace("[ssid]", ssid).replace("[uid]", uid)
                .replace("[uname]", username).replace("[loginname]", loginName)
                .replace("[password]", password).replace("[pw5]", pw5)
                .replace("[verifytoken]", verifytoken);
    }


    /**
     * ??????webFileServer
     *
     * @return
     */
    public static String getWebFileServer() {
        String webServer = QDLanderInfo.getInstance().getWebFileServer();
        if (TextUtils.isEmpty(webServer)) {
            return "http://" + QDLanderInfo.getInstance().getAddress() + ":9001";
        } else {
            return webServer;
        }
    }

    /**
     * ??????webApiServer
     *
     * @return
     */
    public static String getWebApiServer() {
        String webServer = QDLanderInfo.getInstance().getWebApiServer();
        if (TextUtils.isEmpty(webServer)) {
            return "http://" + QDLanderInfo.getInstance().getAddress() + ":9001";
        } else {
            return webServer;
        }
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public static String getFileServer() {
        String fileServer = QDLanderInfo.getInstance().getFileServer();
        if (TextUtils.isEmpty(fileServer)) {
            return QDLanderInfo.getInstance().getAddress() + ":5557";
        } else {
            return fileServer;
        }
    }

    //???dp?????????px
    public static int dp2px(Context context, int value) {
        // ???????????????????????????????????????????????????????????? dp???dip???
        // ??????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????Metrics????????????????????????????????????density??????????????????
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
    }

    //???sp?????????px
    public static int sp2px(Context context, int value) {
        // ???????????????????????????????????????????????????????????? dp???dip???
        // ??????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????Metrics????????????????????????????????????density??????????????????
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
                context.getResources().getDisplayMetrics());
    }

    /**
     * ???????????????
     *
     * @param context
     * @param msg
     */
    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public static void showToastCenter(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(msg);
        toast.show();
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public static boolean isToLoginActivity() {
        String account = QDLanderInfo.getInstance().getAccount();
        String password = QDLanderInfo.getInstance().getPassword();
        String address = QDLanderInfo.getInstance().getAddress();
        int port = QDLanderInfo.getInstance().getPort();
        boolean isLogin = QDLanderInfo.getInstance().isLogin();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || TextUtils.isEmpty(address) || port == 0 || !isLogin) {
            return true;
        }
        return false;
    }

    /**
     * ???????????????
     *
     * @param str
     * @return
     */
    public static String decodeString(String str) {
        String msg = "";
        try {
            msg = new String(Base64.decode(str.getBytes(), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(msg)) {
            return str;
        }
        return msg;
    }

    /**
     * ???????????????
     *
     * @param str
     * @return
     */
    public static String encoderString(String str) {
        String encode = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        return encode;
    }

    /**
     * ??????????????????
     *
     * @param context
     * @param sex
     * @return
     */
    public static String getUserSex(Context context, int sex) {
        if (sex == 1) {
            return context.getResources().getString(R.string.sex_male);
        } else if (sex == 2) {
            return context.getResources().getString(R.string.sex_female);
        }
        return context.getResources().getString(R.string.sex_unknown);
    }

    /**
     * list??????
     *
     * @param list
     * @return
     */
    public static List removeDuplicate(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    /**
     * ??????????????????????????????
     *
     * @param str
     * @return
     */
    public static String getInitial(String str) {
        return str.substring(0, 1);
    }

    /**
     * ????????????????????????
     *
     * @param context
     * @param width
     * @param menu
     * @param color
     * @return
     */
    public static SwipeMenuItem createMenuItem(Context context, int width, String menu, int color) {
        SwipeMenuItem menuItem = new SwipeMenuItem(context);
        menuItem.setBackground(new ColorDrawable(color));
        menuItem.setWidth(width);
        menuItem.setTitle(menu);
        menuItem.setTitleSize(16);
        menuItem.setTitleColor(Color.WHITE);
        return menuItem;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public static String getUserStatus(Context context, String userId) {
        return context.getString(QDResourceUtil.getStringId(context, "user_status_" + QDClient.getInstance().getUserStatus(userId)));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param context
     * @param fileName
     * @return
     */
    public static int getFileIconByName(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return R.mipmap.ic_file_unkown;
        }
        if (!fileName.contains(".")) {
            return R.mipmap.ic_file_unkown;
        }
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        int resID = QDResourceUtil.getMipmapId(context, "ic_file_" + ext);
        if (resID == 0) {
            return R.mipmap.ic_file_unkown;
        }
        return resID;
    }

    public static String getFileType(String filePath) {
        if (!filePath.contains(".")) {
            return "";
        }
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1);
        return ext;
    }

    public static String getSuffix(String path) {
        if (!path.contains(".")) {
            return "";
        }
        String ext = path.substring(path.lastIndexOf("."));
        return ext;
    }

    public static String getErrorMsg(Context context, int errorCode) {
        int resId = QDResourceUtil.getStringId(context, "error_code_" + errorCode);
        return context.getResources().getString(resId);
    }

    /**
     * ????????????????????????
     *
     * @param fileSize
     * @return
     */
    public static String changFileSizeToString(String fileSize) {
        long size = 0;
        if (!TextUtils.isEmpty(fileSize) && TextUtils.isDigitsOnly(fileSize)) {
            size = Long.valueOf(fileSize);
        }
        return changFileSizeToString(size);
    }

    /**
     * ????????????????????????
     *
     * @param fileSize
     * @return
     */
    public static String changFileSizeToString(long fileSize) {
        String size = "";
        if (fileSize > 1024 * 1024 * 1024) {
            size = new DecimalFormat("####.##").format((fileSize >>> 20) / 1024.0) + "GB";
        } else if (fileSize > 1024 * 1024) {
            size = new DecimalFormat("####.##").format((fileSize >>> 10) / 1024.0) + "MB";
        } else if (fileSize > 1024) {
            size = new DecimalFormat("####.##").format(((fileSize)) / 1024.0) + "KB";
        } else {
            size = fileSize + "B";
        }
        return size;
    }

    /**
     * ???????????????
     *
     * @param mEditText
     * @param mContext
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * ???????????????
     *
     * @param mEditText ?????????
     * @param mContext  ?????????
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }

    public static void closeKeybord(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(((Activity) context).getCurrentFocus()).getWindowToken(), 0);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param activity
     * @return
     */
    public static boolean isSoftInputShow(Activity activity) {

        // ?????????????????? ??????view????????????
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            return inputmanger.isActive() && activity.getWindow().getCurrentFocus() != null;
        }
        return false;
    }

    /**
     * ??????????????????,?????????s
     *
     * @param path
     * @return
     */
    public static int getRecordFileDuration(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            float length = (float) mediaPlayer.getDuration() / (float) 1000;
            mediaPlayer.stop();
            mediaPlayer.release();
            if (length > 0 && length < 1) {
                return 1;
            } else {
                return Math.round(length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String secondToMinutes(int seconds) {
        int temp;
        StringBuffer sb = new StringBuffer();
//        temp = seconds / 3600;
//        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 % 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    public static String secondToMinutes(float seconds) {
        int temp;
        StringBuffer sb = new StringBuffer();
//        temp = seconds / 3600;
//        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = (int) (seconds % 3600 / 60);
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = (int) (seconds % 3600 % 60);
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    /**
     * ??????????????????????????????
     *
     * @param path
     * @return
     */
    public static Bitmap getVideoFirstPic(String path) {
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
            Bitmap bitmap = media.getFrameAtTime();
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ????????????
     *
     * @param context
     * @param request
     * @param file
     * @return
     */
    public static String startTakePhoto(Activity context, int request, File file) {
        try {
            //????????????
            HiPermission.create(context).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void onGuarantee(String permisson, int position) { // ??????/?????????
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri = getUri(context, file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    context.startActivityForResult(intent, request);

                }

                @Override
                public void onClose() { // ????????????????????????
                }

                @Override
                public void onFinish() { // ????????????????????????
                }

                @Override
                public void onDeny(String permisson, int position) { // ??????
                }
            });
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getUri(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * 7.0 ??????????????????????????????????????????Uri????????????????????????FileProvider
             * ????????????????????????MIUI?????????????????????size???0?????????
             */
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        }
        return uri;
    }

    public static boolean savePicToGallery(Context context, File file) {
        //??????????????????????????????
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
            //????????????????????????????????????????????????
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * ??????????????????
     *
     * @param context
     * @param file
     * @param request
     * @return
     */
    public static String startPhotoZoom(Activity context, File file, int request) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * 7.0 ??????????????????????????????????????????Uri????????????????????????FileProvider
             * ????????????????????????MIUI?????????????????????size???0?????????
             */
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(uri, "image/*");
        // ????????????
        intent.putExtra("crop", "true");
        // aspectX aspectY ??????????????????
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY ?????????????????????
        intent.putExtra("outputX", 160);
        intent.putExtra("outputY", 160);
//        intent.putExtra("return-data", true);

        String tempPath = QDStorePath.IMG_PATH + System.currentTimeMillis() + ".png";
        Uri tempUri = Uri.parse("file://" + "/" + tempPath);
//        File tempFile = new File(tempPath);
//        Uri tempUri;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            tempUri = Uri.fromFile(tempFile);
//        } else {
//            tempUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", tempFile);
//        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        context.startActivityForResult(intent, request);
        return tempPath;
    }

    /**
     * ??????Bitmap?????????SD??????
     * ????????????SD?????????????????????
     *
     * @param mbitmap ???????????????Bitmap??????
     * @return ??????????????????????????????????????????????????????null
     */
    public static String savePhotoToSD(Bitmap mbitmap, String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file.getAbsoluteFile());
            // ????????????????????????100???????????????
            mbitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    // ?????????????????????
                    outStream.close();
                }
                if (mbitmap != null) {
                    mbitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ????????????1/10???????????????
     *
     * @param path ???????????????
     * @return ??????????????????
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10;  // ?????????????????????????????????????????????
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }


    /**
     * ????????????????????????
     *
     * @param originpath ????????????
     * @param context    ?????????
     * @return ????????????????????????????????????
     */
    public static String amendRotatePhoto(String originpath, Context context) {

        // ????????????????????????
        int angle = readPictureDegree(originpath);

        // ????????????????????????Bitmap??????
        Bitmap bmp = BitmapFactory.decodeFile(originpath);

        // ??????????????????????????????
        Bitmap bitmap = rotaingImageView(angle, bmp);

        // ?????????????????????????????????????????????????????????

        return savePhotoToSD(bitmap, originpath);
    }

    /**
     * ????????????????????????
     *
     * @param path ????????????
     * @return ??????
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * ????????????
     *
     * @param angle  ???????????????
     * @param bitmap ????????????
     * @return ??????????????????
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // ???????????????????????????????????????
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // ?????????????????????????????????????????????????????????????????????
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * ??????????????????
     *
     * @param context
     * @param permission
     */
    public static void getPermission(final Context context, final String... permission) {
        AndPermission.with(context)
                .runtime()
                .permission(permission)
                .rationale(rationale)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        getPermission(context, permission);
                    }
                })
                .start();
    }

    private static Rationale rationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, final RequestExecutor executor) {
            final QDAlertView alertDialog = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Alert)
                    .setContent(context.getResources().getString(R.string.permission_hint))
                    .isHaveCancelBtn(false)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            executor.execute();
                        }
                    })
                    .build();
            alertDialog.show();

        }
    };

    /**
     * Q
     * ???????????????????????????
     *
     * @param activity
     * @param url
     */
    public static void toBrowser(Activity activity, String url) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_VIEW);
        activity.startActivity(intent);
    }

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    /**
     * ?????????????????????1????????????3????????????2????????????-1??????????????????
     *
     * @return int
     */
    public static int getSystem() {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                return 2;//??????
            } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                    || prop.getProperty(KEY_EMUI_VERSION, null) != null
                    || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                return 1;//??????
            } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
                return 3;//??????
            }
        } catch (IOException e) {
            e.printStackTrace();
            return getBrand();
        }
        return getBrand();
    }

    private static int getBrand() {
        String brand = Build.BRAND;
        if (brand.equalsIgnoreCase("xiaomi")) {
            return 2;
        } else if (brand.equalsIgnoreCase("huawei") || brand.equalsIgnoreCase("HONOR")) {
            return 1;
        } else if (brand.equalsIgnoreCase("meizu")) {
            return 3;
        } else {
            return -1;
        }
    }

    private static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception ignored) {
        }
        return defaultValue;
    }


    /**
     * ??????????????????
     *
     * @param count
     * @param notification
     */
    public static void setXiaomiBadge(int count, Notification notification, Context context) {
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, count);
            setUnreadNum(context, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addUserInfo(Context context, String info) {
        SharedPreferences sp = context.getSharedPreferences(
                "com.qd.longchat", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit;
        if (sp != null && (edit = sp.edit()) != null) {
            edit.putString(QDLanderInfo.getInstance().getId(), info);
            edit.apply();
        }
    }

    public static String getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                "com.qd.longchat", Context.MODE_PRIVATE);
        return sp.getString(QDLanderInfo.getInstance().getId(), "");
    }

    /**
     * ????????????????????????
     *
     * @param context
     * @param num
     */
    public static void setUnreadNum(Context context, int num) {
        SharedPreferences sp = context.getSharedPreferences(
                "com.qd.longchat", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit;
        if (sp != null && (edit = sp.edit()) != null) {
            edit.putInt("unreadNum", num);
            edit.apply();
        }
    }

    /**
     * ????????????????????????
     *
     * @param context
     */
    public static int getUnreadNum(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                "com.qd.longchat", Context.MODE_PRIVATE);
        return sp.getInt("unreadNum", 0);
    }

    /**
     * ????????????????????????
     *
     * @param context
     * @param num
     */
    public static void setHuaweiBadge(Context context, int num) {
        try {
            Bundle extra = new Bundle();
            extra.putString("package", "com.qd.longchat");
            extra.putString("class", "com.qd.longchat.activity.QDSplashActivity");
            extra.putInt("badgenumber", num);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????
     *
     * @param context
     * @return
     */
    public static QDLoginParams getLoginParams(Context context) {
        QDLoginParams params = new QDLoginParams();
        params.setDomainName("Default");
        params.setPlatform(QDConst.PLATFORM_ANDROID);
        params.setUpdatePlatform(QDConst.UPDATE_PLATFORM_ANDROID);
        params.setLoginFlag(0);//????????????
        String ip = com.longchat.base.util.QDUtil.getIPAddress(context);
        params.setDeviceIP(ip);
        String deviceID = Build.SERIAL;
        StringBuffer sb = new StringBuffer();
        sb.append(deviceID).append(",")
                .append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append(",")
                .append("android").append(Build.VERSION.RELEASE);
        params.setDeviceInfo(sb.toString());
        String version = "";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        params.setClientVer(version);
        return params;
    }

    public static void createBitmapFromCamera(String path) {
        int reqWidth = 612;
        int reqHeght = 816;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int picWidth = options.outWidth;
        int picHeight = options.outHeight;

        options.inSampleSize = 1;
        if (picWidth >= picHeight && picWidth > reqWidth) {
            options.inSampleSize = picWidth / reqWidth;
        } else {
            if (picHeight > picWidth && picHeight > reqHeght) {
                options.inSampleSize = picHeight / reqHeght;
            }
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        File file = new File(path);
        file.deleteOnExit();
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            savePhotoToSD(scaleBitmap, path);
            scaleBitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
            savePhotoToSD(bitmap, path);
        }
        bitmap.recycle();
    }


    /**
     * ??????client??????
     */
    public static boolean isHaveClientAce(int ace) {
        int clientFlag = QDLanderInfo.getInstance().getClientFlag();
        return (clientFlag & ace) != 0;
    }

    /**
     * ??????????????????
     */
    public static boolean isHaveSelfAce(int ace1, int ace2) {
        return (ace1 & ace2) != 0;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            return isChinese(c);
        }
        return false;
    }

    /**
     * ???????????????????????????
     */
    public static void setSpeakMode(Context context, boolean b) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (b) {
            // ????????????????????????false
            audioManager.setSpeakerphoneOn(false);
            // ?????????????????????
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            // ?????????????????????
            ((Activity) context).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        } else {
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    /**
     * ????????????????????????
     *
     * @param email
     * @return
     */
    public static boolean isMatchEmail(String email) {
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /*?????????2018???3?????????
    ??????????????????
        133,149,153,173,177,180,181,189,199
    ??????????????????
        130,131,132,145,155,156,166,175,176,185,186
    ??????????????????
        134(0-8),135,136,137,138,139,147,150,151,152,157,158,159,178,182,183,184,187,188,198
    ????????????
        14????????????????????????????????????????????????????????????145?????????????????????147?????????
        ???????????????
            ?????????1700,1701,1702
            ?????????1703,1705,1706
            ?????????1704,1707,1708,1709,171
        ???????????????148(??????) 1349
    */

    /**
     * ???????????????????????????
     *
     * @param str
     * @return
     */
    public static boolean isMatchMobile(String str) {
        Pattern p;
        Matcher m;
        boolean b;
        String s2 = "^[1](([3][0-9])|([4][5,7,9])|([5][^4,6,9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$";// ???????????????
        p = Pattern.compile(s2);
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

//    /**
//     * ???????????????md5
//     * @param file
//     * @return
//     */
//    public static String getFileMD5(File file) {
//        if (!file.isFile()) {
//            return null;
//        }
//        MessageDigest digest = null;
//        FileInputStream in = null;
//        byte buffer[] = new byte[1024];
//        int len;
//        try {
//            digest = MessageDigest.getInstance("MD5");
//            in = new FileInputStream(file);
//            while ((len = in.read(buffer, 0, 1024)) != -1) {
//                digest.update(buffer, 0, len);
//            }
//            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        BigInteger bigInt = new BigInteger(1, digest.digest());
//        return bigInt.toString(16).toLowerCase();
//    }


    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F'};

    /**
     * ???????????????md5???
     *
     * @param file
     * @return
     */
    public static String getFileMd5(File file) {
        InputStream fis = null;
        byte[] buffer = new byte[8192];
        int numRead = 0;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            return toHexString(md5.digest()).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                QDLog.e("123", "", e);
            }
        }
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * ????????????????????????????????????
     *
     * @param phoneNum ????????????
     */
    @SuppressLint("MissingPermission")
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public static String getServerTime() {
        long offset = QDClient.getInstance().getLoginInfo().getSTimeOffset();
        long serverTime = System.currentTimeMillis() + offset;
        String sTime = QDDateUtil.dateToString(new Date(serverTime), QDDateUtil.MSG_FORMAT4);
        return sTime;
    }

    /**
     * ??????????????????
     *
     * @param oldPath String ??????????????? ??????c:/fqf.txt
     * @param newPath String ??????????????? ??????f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return;
        }
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //???????????????
                InputStream inStream = new FileInputStream(oldPath); //???????????????
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //????????? ????????????
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("??????????????????????????????");
            e.printStackTrace();

        }
    }

    /**
     * ????????????
     *
     * @param context
     * @param path
     */
    public static void openFile(Context context, String path) {
        if (path.toLowerCase().endsWith("jpg") || path.toLowerCase().endsWith("jpeg") || path.toLowerCase().endsWith("png") || path.toLowerCase().endsWith("bmp") || path.toLowerCase().endsWith("gif")) {
            Intent i = new Intent(context, QDPicActivity.class);
            List<String> pathList = new ArrayList<>(1);
            pathList.add(path);
            i.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
            context.startActivity(i);
        } else if (path.toLowerCase().endsWith("rm") || path.toLowerCase().endsWith("rmvb") || path.toLowerCase().endsWith("mp4") || path.toLowerCase().endsWith("avi")
                || path.toLowerCase().endsWith("wav") || path.toLowerCase().endsWith("wmv") || path.toLowerCase().endsWith("mpg") || path.toLowerCase().endsWith("mov")
                || path.toLowerCase().endsWith("swf") || path.toLowerCase().endsWith("flv") || path.toLowerCase().endsWith("3gp")) {
            Intent intent = new Intent(context, QDPlayerVideoActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SHOOT_PATH, path);
            context.startActivity(intent);
        } else if (path.toLowerCase().endsWith("wav") || path.toLowerCase().endsWith("aif") || path.toLowerCase().endsWith("au") || path.toLowerCase().endsWith("mp3")
                || path.toLowerCase().endsWith("ram") || path.toLowerCase().endsWith("wma") || path.toLowerCase().endsWith("mmf") || path.toLowerCase().endsWith("amr")
                || path.toLowerCase().endsWith("aac") || path.toLowerCase().endsWith("flac")) {
            Intent intent = new Intent(context, QDPlayMusicActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, path);
            context.startActivity(intent);
        } else if (path.toLowerCase().endsWith("apk")) {
            AndPermission.with(context)
                    .install()
                    .file(new File(path))
                    .start();
        } else {
            Intent i = new Intent(context, QDFileDisplayActivity.class);
            i.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, path);
            context.startActivity(i);
        }
    }

    /**
     * ??????listview???????????????????????????
     *
     * @param context
     * @param resInt
     * @param warnText
     * @param listView
     */
    public static void setEmptyView(Context context, int resInt, String warnText, ListView listView) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_list_empty, null);
        TextView textView = view.findViewById(R.id.tv_item_warn);
        ImageView imageView = view.findViewById(R.id.iv_item_icon);
        if (resInt != 0) {
            imageView.setImageResource(resInt);
        }
        if (!TextUtils.isEmpty(warnText)) {
            textView.setText(warnText);
        }
        ((ViewGroup) listView.getParent()).addView(view);
        listView.setEmptyView(view);
    }

    /**
     * map???string
     *
     * @param map
     * @return
     */
    public static String mapToString(HashMap<String, String> map) {
        StringBuffer sb = new StringBuffer();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            sb.append(entry.getKey()).append("|").append(entry.getValue());
            if (it.hasNext()) {
                sb.append("^");
            }
        }
        return sb.toString();
    }

    /**
     * string???map
     *
     * @param string
     * @return
     */
    public static HashMap<String, String> stringToMap(String string) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        StringTokenizer items = new StringTokenizer(string, "^");
        while (items.hasMoreTokens()) {
            StringTokenizer item = new StringTokenizer(items.nextToken(), "|");
            String key = item.nextToken();
            String value = item.nextToken();
            map.put(key, value);
        }
        return map;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public static String readDraftFile() {
        try {
            File file = new File(QDStorePath.TEMP_PATH + "_" + DRAFT_FILE);
            if (!file.exists()) {
                return "";
            }
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder();
            while ((hasRead = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead));
            }
            inputStream.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ?????????????????????
     *
     * @param msg
     */
    public static void writeDraftFile(String msg) {
        try {
            File file = new File(QDStorePath.TEMP_PATH + "_" + DRAFT_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(msg.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String handleImageOnKitKat(Context context, Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                String type = docId.split(":")[0];
                Uri contentUri = null;
                if (type.equalsIgnoreCase("image")) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if (type.equalsIgnoreCase("audio")) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else if (type.equalsIgnoreCase("video")) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                return getImagePath(context, contentUri, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                return getImagePath(context, contentUri, null);
            } else if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String type = docId.split(":")[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + docId.split(":")[1];
                }
            } else if ("content".equals(uri.getAuthority())) {
                return getImagePath(context, uri, null);
            } else if ("file".equals(uri.getAuthority())) {
                return uri.getPath();
            }
        } else {
            return handleImageBeforeKitKat(context, data);
        }
        return "";
    }

    public static String handleImageBeforeKitKat(Context context, Intent data) {
        Uri uri = data.getData();
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getImagePath(context, uri, null);
        } else if ("file".equals(uri.getScheme())) {
            return uri.getPath();
        }
        return getImagePath(context, uri, null);
    }

    private static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String p = uri.getPath();
            String[] strs = p.split("/");
            StringBuilder sb = new StringBuilder();
            if (strs.length > 2) {
                for (int i = 2; i < strs.length; i++) {
                    sb.append("/").append(strs[i]);
                }
            }
            return Environment.getExternalStorageDirectory() + sb.toString();
        }
        return path;
    }

    public static Drawable getBoundedDrawable(Context context, int res) {
        Drawable draw = context.getResources().getDrawable(res);
        if (draw != null) {
            draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
        }
        return draw;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public static boolean isSecret(QDUser user) {
        String userId = user.getId();
        boolean isFriend = QDFriendHelper.isFriend(userId);
        if (isFriend) {
            return false;
        } else {
            int level = user.getLevel();
            if (level >= QDLanderInfo.getInstance().getLevel()) {
                return false;
            } else {
                return true;
            }
        }
    }

}