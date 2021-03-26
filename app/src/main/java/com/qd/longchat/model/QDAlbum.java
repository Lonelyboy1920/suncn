package com.qd.longchat.model;

import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/4 下午12:00
 * 相册对象
 */

public class QDAlbum {

    private String id;
    private String name;
    private List<QDPhoto> photoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QDPhoto> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<QDPhoto> photoList) {
        this.photoList = photoList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
