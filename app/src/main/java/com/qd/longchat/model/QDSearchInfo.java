package com.qd.longchat.model;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/5 下午3:18
 */
public class QDSearchInfo {

    public final static int ITEM_TYPE_PERSON_CHAT = 0;
    public final static int ITEM_TYPE_GROUP_CHAT = 1;
    public final static int ITEM_TYPE_USER = 2;
    public final static int ITEM_TYPE_GROUP = 3;

    private String id;
    private String name;
    private String subname;
    private String icon;
    private int type;
    private long time;
    private boolean isMore;
    private String title;
    private int itemType;
    private int count;
    private String moreText;
    private String infoText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMoreText() {
        return moreText;
    }

    public void setMoreText(String moreText) {
        this.moreText = moreText;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }
}
