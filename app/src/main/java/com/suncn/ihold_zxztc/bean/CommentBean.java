package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by moos on 2018/4/20.
 */

public class CommentBean {
    private int code;
    private String message;
    private Data data;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public class Data {

        private int total;
        private ArrayList<CommentDetailBean> list;

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotal() {
            return total;
        }

        public void setList(ArrayList<CommentDetailBean> list) {
            this.list = list;
        }

        public ArrayList<CommentDetailBean> getList() {
            return list;
        }

    }

}
