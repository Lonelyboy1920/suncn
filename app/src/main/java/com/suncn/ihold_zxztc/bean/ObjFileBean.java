package com.suncn.ihold_zxztc.bean;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;

/**
 * 正文附件实体类
 */
public class ObjFileBean implements Serializable {
    private String strFile_name; // 文件名 第一组为正文附件，无正文时值为“”
    private String strFile_url; // 文件 URL 第一组为正文附件，无正文时值为“”
    private String strFile_size; // 文件大小
    private String strFile_id; // 文件ID
    private String strFile_thumb;//缩略图
    private File file; // 附件
    private String strFile_Type; // 文件类型
    private Bitmap thumb;
    private int position; // 该附件在gridview中的位置

    public String getStrFile_thumb() {
        return strFile_thumb;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getStrFile_Type() {
        return strFile_Type;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public void setStrFile_Type(String strFile_Type) {
        this.strFile_Type = strFile_Type;
    }

    public String getStrFile_name() {
        return strFile_name;
    }

    public void setStrFile_name(String strFile_name) {
        this.strFile_name = strFile_name;
    }

    public String getStrFile_url() {
        return strFile_url;
    }

    public void setStrFile_url(String strFile_url) {
        this.strFile_url = strFile_url;
    }

    public String getStrFile_size() {
        return strFile_size;
    }

    public void setStrFile_size(String strFile_size) {
        this.strFile_size = strFile_size;
    }

    public String getStrFile_id() {
        return strFile_id;
    }

    public void setStrFile_id(String strFile_id) {
        this.strFile_id = strFile_id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
