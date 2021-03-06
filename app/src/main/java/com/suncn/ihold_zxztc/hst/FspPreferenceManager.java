package com.suncn.ihold_zxztc.hst;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.suncn.ihold_zxztc.MyApplication;

public class FspPreferenceManager {

    private static FspPreferenceManager s_instance = null;

    private SharedPreferences.Editor m_editor;
    private SharedPreferences m_sharedPreferences;


    public FspPreferenceManager() {
        m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
        m_editor = m_sharedPreferences.edit();
    }

    public static FspPreferenceManager getInstance() {
        if (s_instance == null) {
            synchronized (FspPreferenceManager.class) {
                if (s_instance == null) {
                    s_instance = new FspPreferenceManager();
                }
            }
        }
        return s_instance;
    }

    public boolean getAppConfig() {
        return m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_APPCONFIG, true);
    }

    public String getAppId() {
        return m_sharedPreferences.getString(FspConstants.PKEY_USER_APPID, "");
    }

    public String getAppSecret() {
        return m_sharedPreferences.getString(FspConstants.PKEY_USER_APPSECRET, "");
    }

    public String getAppServerAddr() {
        return m_sharedPreferences.getString(FspConstants.PKEY_USER_APPSERVERADDR, "");
    }

    public boolean getDefaultOpenCamera() {
        return m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_OPENCAMERA, false);
    }

    public boolean getDefaultOpenMIC() {
        return m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_OPENMIC, false);
    }

    public FspPreferenceManager setAppConfig(boolean config) {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_APPCONFIG, config);
        return this;
    }

    public FspPreferenceManager setAppId(String appId) {
        m_editor.putString(FspConstants.PKEY_USER_APPID, appId);
        return this;
    }

    public FspPreferenceManager setAppSecret(String appId) {
        m_editor.putString(FspConstants.PKEY_USER_APPSECRET, appId);
        return this;
    }

    public FspPreferenceManager setAppServerAddr(String appServerAddr) {
        m_editor.putString(FspConstants.PKEY_USER_APPSERVERADDR, appServerAddr);
        return this;
    }

    public FspPreferenceManager setDefaultOpenCamera(boolean open) {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_OPENCAMERA, open);
        return this;
    }

    public FspPreferenceManager setDefaultOpenMIC(boolean open) {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_OPENMIC, open);
        return this;
    }

    public void apply() {
        m_editor.apply();
    }

    public void commit() {
        m_editor.commit();
    }

}
