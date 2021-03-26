package com.qd.longchat.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/7 下午1:36
 */
public class QDOpenWindowBean {

    /**
     * title :
     * url : http://www.baidu.com
     * bgcolor : #000000
     * color : #ffffff
     * hidden : false
     * type : 0
     * leftMenu : {"icon":"back","text":"返回","event":"jscallback(1)"}
     * rightMenu : {"icon":"add","text":"添加","event":"jscallback(2)"}
     */

    private String url;
    @SerializedName("bgcolor")
    private String bgColor;
    private String color;
    private boolean hidden;
    private int type;
    private String title;
    private LeftMenuBean leftMenu;
    private RightMenuBean rightMenu;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LeftMenuBean getLeftMenu() {
        return leftMenu;
    }

    public void setLeftMenu(LeftMenuBean leftMenu) {
        this.leftMenu = leftMenu;
    }

    public RightMenuBean getRightMenu() {
        return rightMenu;
    }

    public void setRightMenu(RightMenuBean rightMenu) {
        this.rightMenu = rightMenu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class LeftMenuBean {
        /**
         * icon : back
         * text : 返回
         * event : jscallback(1)
         */

        private String icon;
        private String text;
        private String event;
        private String closePage;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getClosePage() {
            return closePage;
        }

        public void setClosePage(String closePage) {
            this.closePage = closePage;
        }
    }

    public static class RightMenuBean {
        /**
         * icon : add
         * text : 添加
         * event : jscallback(2)
         */

        private String icon;
        private String text;
        private String event;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }
    }
}
