package com.qd.longchat.model;

import com.longchat.base.dao.QDMessage;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/23 下午6:21
 */
public class QDChatModel {
    private int mode;
    private QDMessage message;

    public QDChatModel(int mode, QDMessage message) {
        this.mode = mode;
        this.message = message;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public QDMessage getMessage() {
        return message;
    }

    public void setMessage(QDMessage message) {
        this.message = message;
    }
}
