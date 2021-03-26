package com.qd.longchat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author Gao
 * date 2017/7/4 0004
 * description 百度地图定位Activity之间用来传输的bean
 */

public class QDLocationBean implements Parcelable {

    private double latitude;
    private double longitude;
    private String sampleLocationInfo;
    private String detailLocationInfo;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSampleLocationInfo() {
        return sampleLocationInfo;
    }

    public void setSampleLocationInfo(String sampleLocationInfo) {
        this.sampleLocationInfo = sampleLocationInfo;
    }

    public String getDetailLocationInfo() {
        return detailLocationInfo;
    }

    public void setDetailLocationInfo(String detailLocationInfo) {
        this.detailLocationInfo = detailLocationInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.sampleLocationInfo);
        dest.writeString(this.detailLocationInfo);
    }

    public QDLocationBean() {
    }

    protected QDLocationBean(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.sampleLocationInfo = in.readString();
        this.detailLocationInfo = in.readString();
    }

    public static final Creator<QDLocationBean> CREATOR = new Creator<QDLocationBean>() {
        @Override
        public QDLocationBean createFromParcel(Parcel source) {
            return new QDLocationBean(source);
        }

        @Override
        public QDLocationBean[] newArray(int size) {
            return new QDLocationBean[size];
        }
    };
}
