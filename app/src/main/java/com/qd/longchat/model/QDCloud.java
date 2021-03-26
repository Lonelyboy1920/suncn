package com.qd.longchat.model;

import java.io.Serializable;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/16 下午2:02
 */
public class QDCloud implements Serializable {
    public final static int TYPE_STATING = 0; //首页
    public final static int TYPE_PERSON_CLOUD = 1; //个人云盘
    public final static int TYPE_COMPANY_CLOUD = 2; //企业云盘
    public final static int TYPE_FOLDER = 3; //文件夹
    public final static int TYPE_FILE = 4;
    public final static int TYPE_CLOUD = 5;

    public final static int STATUS_NORMAL = 0; //正常状态
    public final static int STATUS_UPLOAD = 1; //上传状态
    public final static int STATUS_DOWNLOAD = 2; //下载状态
    public final static int STATUS_PAUSE_UPLOAD = 3; //暂停上传状态
    public final static int STAUTS_PAUSE_DOWNLOAD = 4; //暂停下载状态

    public final static int ACE_DOWNLOAD = 1;
    public final static int ACE_CREATE = 2;
    public final static int ACE_MODIFY = 4;

    private String rootId;
    private String pid;
    private String id;
    private String name;
    private String originalUrl;
    private String createrId;
    private String createrName;
    private long size;
    private long createTime;
    private int type;
    private int status;
    private String path;
    private String md5;
    private String ext;
    private int power;

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
