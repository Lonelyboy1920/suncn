package com.qd.longchat.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.manager.QDSelfInfoManager;
import com.longchat.base.model.QDLoginInfo;
import com.longchat.base.util.QDConst;
import com.qd.longchat.R;
import com.qd.longchat.util.QDHttpUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDWaitingDialog;
import com.yanzhenjie.permission.AndPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by xxw on 2016/12/27.
 */

public class QDUpgrade {

    private Context context;

    private String newVersion;
    private String downloadUrl;
    private QDWaitingDialog waitingDialog;
    private Handler handler;
    private String apkPath;
    private boolean isCanceled = false;

    public QDUpgrade(Context context) {
        this.context = context;
        handler = new Handler();
    }

    /**
     * 检查版本
     */
    public void checkUpgrade() {
        if (!QDClient.getInstance().isOnline()) {
            return;
        }
        newVersion = QDClient.getInstance().getLoginInfo().getConfig(QDLoginInfo.packver);
        if (TextUtils.isEmpty(newVersion)) {
            return;
        }
        String ver = newVersion.replace(".", "");
        if (!TextUtils.isDigitsOnly(ver)) {
            return;
        }
        int sVer = Integer.valueOf(ver);
        try {
            //获取当前应用的版本
            String localVer = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).versionName;
            localVer = localVer.replace(".", "");
            int lVer = Integer.valueOf(localVer);
            //比较服务器版本与本地版本的大小
            if (sVer > lVer) {
                //若可以升级,则从web服务器上获取升级包地址
                QDSelfInfoManager.getInstance().getUpdateVer(new QDResultCallBack<String>() {
                    @Override
                    public void onError(String errorMsg) {

                    }

                    @Override
                    public void onSuccess(String url) {
                        downloadUrl = url;
                        showUpgradeDialog();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从服务器上获取更新包的地址
     */
    private void getAppUrlFromServer() {
        if (QDClient.getInstance().getLoginInfo() == null) {
            return;
        }
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String ssid = QDClient.getInstance().getLoginInfo().getSSID();
                String uid = QDClient.getInstance().getLoginInfo().getUserID();
                String uName = QDClient.getInstance().getLoginInfo().getUserName();
                String appid = QDClient.getInstance().getLoginInfo().getConfig(QDLoginInfo.APP_ID);
                String authen = QDClient.getInstance().getLoginInfo().getAuthen();

                String param = "ssid=" + ssid + "&uid=" + uid + "&uname=" + uName + "&app_id=" + appid + "&authen=" + authen + "&app_type=" + QDConst.UPDATE_PLATFORM_ANDROID;
                String result = QDHttpUtil.sendGet(QDLanderInfo.getInstance().getWebApiServer() + "/api/common/upgrade.html", param);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject data = object.getJSONObject("data");
                    downloadUrl = data.getString("file_path");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //成功获取后提醒用户是否升级app
                if (!TextUtils.isEmpty(downloadUrl)) {
                    showUpgradeDialog();
                }
                super.onPostExecute(aVoid);
            }
        };
        task.execute();
    }

    /**
     * 展示升级提醒对话框
     */
    private void showUpgradeDialog() {
        String tip = context.getString(R.string.im_new_version_to_upgrade);
        tip = String.format(tip, newVersion);
        new AlertDialog.Builder(context).setTitle(R.string.im_text_reminder).setMessage(tip)
                .setCancelable(false)
                .setPositiveButton(R.string.im_text_upgrade, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //弹出等待对话框
                        waitingDialog = new QDWaitingDialog(context);
                        waitingDialog.setTip(context.getString(R.string.im_downloading_waiting));
                        waitingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                isCanceled = true;
                            }
                        });
                        waitingDialog.show();
                        //下载升级压缩包
                        downloadZip();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * 下载升级包
     */
    private void downloadZip() {
        final String path = QDStorePath.UPGRADE_FOLDER + newVersion + "_Upgrade.zip";
        QDFileManager.getInstance().downloadFile(path, QDUtil.replaceWebServerAndToken(downloadUrl), new QDFileDownLoadCallBack() {
            @Override
            public void onDownLoading(int per) {
                waitingDialog.setTip(context.getString(R.string.im_downloading_waiting) + "(" + per + "%)");
            }

            @Override
            public void onDownLoadSuccess() {
                if (isCanceled) {
                    return;
                }

                //提示正在解压缩
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitingDialog.setTip(context.getString(R.string.im_downloading_unzipping));
                    }
                }, 1000);

                String folder = path.substring(0, path.length() - 4);//解压缩到以zip文件名命名的文件夹中
                UnZipFolder(path, folder);
                if (isCanceled) {
                    return;
                }

                File file = new File(folder);
                File[] fileList = file.listFiles();
                for (File f : fileList) {
                    if (f.getName().toLowerCase().endsWith(".apk")) {
                        apkPath = f.getAbsolutePath();
                    }
                }

                if (TextUtils.isEmpty(apkPath) || !new File(apkPath).exists()) {
                    waitingDialog.dismiss();
                    QDUtil.showToast(context, context.getResources().getString(R.string.im_downloading_unzip_failed));
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            waitingDialog.dismiss();
                            install();
                        }
                    });
                }
            }

            @Override
            public void onDownLoadFailed(String msg) {
                waitingDialog.dismiss();
                QDUtil.showToast(context, context.getResources().getString(R.string.im_text_download_failed));
            }
        });
    }



    /**
     * 解压zip到指定的路径
     * @param zipFileString  ZIP的名称
     * @param outPathString   要解压缩路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString){
        try {
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    //获取部件的文件夹名
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    Log.e("111",outPathString + File.separator + szName);
                    File file = new File(outPathString + File.separator + szName);
                    if (!file.exists()){
                        Log.e("1111", "Create the file:" + outPathString + File.separator + szName);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    // 获取文件的输出流
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // 读取（字节）字节到缓冲区
                    while ((len = inZip.read(buffer)) != -1) {
                        // 从缓冲区（0）位置写入（字节）字节
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    /**
     * 解压缩压缩包
     *
     * @param folder 解压缩出来的内容存放地址
     * @param path   压缩包的地址
     */
    private void unZipping(String folder, String path) { // 方法重载
        try {
            ZipInputStream Zin = new ZipInputStream(new FileInputStream(path));//输入源zip路径
            BufferedInputStream Bin = new BufferedInputStream(Zin);

            File file;
            ZipEntry entry;
            while ((entry = Zin.getNextEntry()) != null && !entry.isDirectory()) {
                file = new File(folder, entry.getName());
                if (!file.exists()) {
                    (new File(file.getParent())).mkdirs();
                }
                if (file.getAbsolutePath().endsWith(".apk") || file.getAbsolutePath().endsWith(".APK")) {
                    apkPath = file.getAbsolutePath();
                }
                FileOutputStream out = new FileOutputStream(file);
                BufferedOutputStream Bout = new BufferedOutputStream(out);
                int b;
                while ((b = Bin.read()) != -1) {
                    Bout.write(b);
                }
                Bout.close();
                out.close();
            }
            Bin.close();
            Zin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开安装程序
     */
    private void install() {

        AndPermission.with(context)
                .install().file(new File(apkPath)).start();
    }

}
