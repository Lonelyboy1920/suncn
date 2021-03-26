package com.suncn.ihold_zxztc.activity.global;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIFileUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.DefineUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 附件选择界面 gavin
 */
public class FileBrowserActivity extends BaseActivity {
    @BindView(id = R.id.list)
    ListView listView; // ListView
    @BindView(id = R.id.tv_path, click = true)
    private TextView pathTextView; // 路劲导航TextView

    // 根目录
    File sdRoot;
    // 存放当前路径下的所有文件及文件夹
    File[] datas;
    // 当前路径
    File nowFile;
    int lastPoint = 0;
    boolean isBack = false;
    MyAdapater adapater;
    // 匹配图标时用到的文件类型
    String[] fileTypes = new String[]{"apk", "avi", "bat", "bin", "bmp", "chm", "css", "dat", "dll", "doc", "docx", "dos", "dvd", "gif", "html",
            "ifo", "inf", "iso", "java", "jpeg", "jpg", "log", "m4a", "mid", "mov", "movie", "mp2", "mp2v", "mp3", "mp4", "mpe", "mpeg", "mpg",
            "pdf", "php", "png", "ppt", "pptx", "psd", "rar", "tif", "ttf", "txt", "wav", "wma", "wmv", "xls", "xlsx", "xml", "xsl", "zip"};

    private boolean isAttach; // 反馈文档附件
    private boolean isScan; // 扫描件

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_filebrowser);
        isShowBackBtn = true;
    }

    @Override
    public void initData() {
        setHeadTitle("选择文件");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isAttach = bundle.getBoolean("isAttach");
            isScan = bundle.getBoolean("isScan");
            GILogUtil.i("isScan===" + isScan + ",,,isAttach==" + isAttach);
        }
        sdRoot = new File("/sdcard");
        if (sdRoot.exists()) {
            loadFiles(sdRoot);
        }
        super.initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_path:
                String parent = nowFile.getParent();
                if (!parent.equals("/")) {
                    isBack = true;
                    loadFiles(new File(parent));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                File clickFile = datas[arg2];
                if (clickFile.isDirectory()) {
                    lastPoint = arg2;
                    loadFiles(clickFile);
                } else {
                    if (GIFileUtil.getFileSize(clickFile.getPath()) > DefineUtil.FILE_SIZE) {
                        showToast(DefineUtil.FILE_MESSAGE);
                    } else {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("file", clickFile);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
        super.setListener();
    }

    void loadFiles(File directory) {
        nowFile = directory;
        if (nowFile.getPath().equals("/sdcard")) {
            pathTextView.setText("内部存储");
        } else {
            pathTextView.setText(nowFile.getPath().replace("/sdcard", "内部存储").replaceAll("/", "\ue77b"));
        }

        // 分类并排序
        File[] temp = directory.listFiles();
        ArrayList<File> tempFolder = new ArrayList<File>();
        ArrayList<File> tempFile = new ArrayList<File>();
        if (temp != null) {
            for (File file : temp) {
                String fName = file.getName();
                String fType = fName.substring(fName.lastIndexOf(".") + 1).toLowerCase();
                if (file.isDirectory() && !fName.substring(0, 1).equals(".") && file.list().length > 0) {
                    tempFolder.add(file);
                } else if (isScan) { // 扫描件
                    if (fType.equals("pdf"))
                        tempFile.add(file);
                } else if (isAttach) { // 文档附件
                    if (fType.equals("doc") || fType.equals("docx") || fType.equals("xls") || fType.equals("xlsx") || fType.equals("txt"))
                        tempFile.add(file);
                } else if (file.isFile() && (fType.equals("txt") || fType.equals("doc") || fType.equals("docx") || fType.equals("xls") || fType.equals("xlsx") || fType.equals("pdf"))) {
                    tempFile.add(file);
                }
            }
        }

        // 对List进行排序
        Comparator<File> comparator = new MyComparator();
        Collections.sort(tempFolder, comparator);
        Collections.sort(tempFile, comparator);

        datas = new File[tempFolder.size() + tempFile.size()];
        System.arraycopy(tempFolder.toArray(), 0, datas, 0, tempFolder.size());
        System.arraycopy(tempFile.toArray(), 0, datas, tempFolder.size(), tempFile.size());
        // 数据处理结束 =========================================
        adapater = new MyAdapater(FileBrowserActivity.this);
        listView.setAdapter(adapater);
        if (isBack) {
            // listView.setSelection(lastPoint);
            listView.smoothScrollToPosition(lastPoint);
            lastPoint = 0;
            isBack = false;
        }

        // adapater.notifyDataSetChanged();
    }

    // 自定义比较器
    class MyComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                String parent = nowFile.getParent();
                if (parent.equals("/")) {
                    finish();
                    return true;
                }
                isBack = true;
                loadFiles(new File(parent));
                return true;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    // private void openFile(File f) {
    // Intent intent = new Intent();
    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // intent.setAction(android.content.Intent.ACTION_VIEW);
    // /* 调用getMIMEType()来取得MimeType */
    // String type = getMIMEType(f);
    // /* 设置intent的file与MimeType */
    // intent.setDataAndType(Uri.fromFile(f), type);
    // startActivity(intent);
    // }

    /* 判断文件MimeType的method */
    // private String getMIMEType(File f) {
    // String type = "";
    // String fName = f.getName();
    // /* 取得扩展名 */
    // String end = fName.substring(fName.lastIndexOf(".") + 1,
    // fName.length()).toLowerCase();
    //
    // /* 依扩展名的类型决定MimeType */
    // if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
    // end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
    // type = "audio";
    // } else if (end.equals("3gp") || end.equals("mp4")) {
    // type = "video";
    // } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
    // end.equals("jpeg") || end.equals("bmp")) {
    // type = "image";
    // } else if (end.equals("apk")) {
    // /* android.permission.INSTALL_PACKAGES */
    // type = "application/vnd.android.package-archive";
    // } else if (end.equals("txt") || end.equals("java")) {
    // /* android.permission.INSTALL_PACKAGES */
    // type = "text";
    // } else {
    // type = "*";
    // }
    // /* 如果无法直接打开，就跳出软件列表给用户选择 */
    // if (end.equals("apk")) {
    // } else {
    // type += "/*";
    // }
    // return type;
    // }

//    ArrayList<File> allFiles = new ArrayList<File>();

//    void getAllFiles(File path) {
//        // 递归取得目录下的所有文件及文件夹
//        File[] files = path.listFiles();
//        for (File file : files) {
//            allFiles.add(file);
//            if (file.isDirectory()) {
//                getAllFiles(file);
//            }
//        }
//    }

//    void deleteFolder(File path) {
//        getAllFiles(path);
//        while (allFiles.size() != 0) {
//            for (int i = 0; i < allFiles.size(); i++) {
//                File file = allFiles.get(i);
//                if (file.isFile()) {
//                    boolean deleted = file.delete();
//                    if (deleted)
//                        allFiles.remove(i);
//                } else if (file.isDirectory()) {
//                    try {
//                        boolean deleted = file.delete();
//                        if (deleted)
//                            allFiles.remove(i);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

    private class ViewHolder {
        TextView nameView;
        TextView sizeView;
        TextView iconView;
        GITextView arrowView;
    }

    private class MyAdapater extends BaseAdapter {
        LayoutInflater mInflater;

        public MyAdapater(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.view_lv_item_filebrowser, null);
                holder = new ViewHolder();
                holder.nameView = convertView.findViewById(R.id.text_name);
                holder.sizeView = convertView.findViewById(R.id.tv_size);
                holder.iconView = convertView.findViewById(R.id.tv_icon);
                holder.arrowView = convertView.findViewById(R.id.tv_arrow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 从arraylist集合里取出当前行数据；
            File file = datas[position];

            // 为页面控件设置数据
            if (file.isDirectory()) {
                holder.sizeView.setVisibility(View.GONE);
                holder.iconView.setText("\ue606");
                holder.iconView.setTextColor(Color.parseColor("#ffce2e"));
                holder.arrowView.setVisibility(View.VISIBLE);
            } else {
                holder.arrowView.setVisibility(View.GONE);
                holder.sizeView.setVisibility(View.VISIBLE);
                holder.sizeView.setText(GIFileUtil.formetFileSize(GIFileUtil.getFileSize(file.getPath())));

                String name = file.getName();
                String icon = "\ue63b";//MIME_MapTable.get(type);
                String myColor = "#9fa8da";
                int pointIndex = name.lastIndexOf(".");
                if (pointIndex != -1) {
                    String type = name.substring(pointIndex + 1).toLowerCase();
                    switch (type) {
                        case "pdf":  //
                            icon = "\ue637";
                            myColor = "#ee544f";
                            break;
                        case "doc":
                        case "docx":
                        case "wps": //
                            icon = "\ue636";
                            myColor = "#1888e5";
                            break;
                        case "xls":
                        case "xlsx":
                        case "et": //
                            icon = "\ue635";
                            myColor = "#7cb442";
                            break;
                        case "pps":
                        case "ppt":
                        case "pptx":
                        case "dps":
                            icon = "\ue647";
                            myColor = "#d24625";
                            break;
                        case "apk":
                            icon = "\ue63c";
                            myColor = "#97c03d";
                            break;
                        case "chm":
                            icon = "\ue646";
                            myColor = "#8d6e63";
                            break;
                        case "bmp":
                        case "gif":
                        case "jpeg":
                        case "jpg":
                        case "png":
                            icon = "\ue641";
                            myColor = "#16c2c2";
                            break;
                        case "htm":
                        case "html":
                            icon = "\ue643";
                            myColor = "#f57c00";
                            break;
                        case "log":
                            icon = "\ue644";
                            myColor = "#9575cd";
                            break;
                        case "txt": //
                            icon = "\ue634";
                            myColor = "#8fa5b5";
                            break;
                        case "xml":
                            icon = "\ue63e";
                            myColor = "#a5bfcb";
                            break;
                        case "3gp":
                        case "asf":
                        case "avi":
                        case "rmvb":
                        case "mov":
                        case "mp4":
                            icon = "\ue639";
                            myColor = "#40c4ff";
                            break;
                        case "mp3":
                        case "wav":
                        case "wma":
                        case "wmv":
                            icon = "\ue63d";
                            myColor = "#f06292";
                            break;
                        case "zip":
                        case "rar": //
                            icon = "\ue635";
                            myColor = "#ffce2e";
                            break;
                        case "ttf":
                            icon = "\ue640";
                            myColor = "#666666";
                            break;
                        case "css":
                            icon = "\ue645";
                            myColor = "#009688";
                            break;
                        default:
                            icon = "\ue63b";
                            myColor = "#9fa8da";
                            break;
                    }
                }
                holder.iconView.setText(icon);
                holder.iconView.setTextColor(Color.parseColor(myColor));
            }
            holder.nameView.setText(file.getName());
            // 标示当前行是否被选中（通过显隐selectView实现）
            return convertView;
        }

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}