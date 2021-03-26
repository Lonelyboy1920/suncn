package com.qd.longchat.bean;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/16 下午3:05
 */
public class QDDownloadBean {

    /**
     * url : http://www.qidainfo.com/public/home/img/logo_black.png
     * size : 99
     * name :
     */

    private String id;
    private String url;
    private long size;
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
