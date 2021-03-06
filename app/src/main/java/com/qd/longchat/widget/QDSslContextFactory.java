package com.qd.longchat.widget;

import android.content.Context;
import android.util.Log;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/7 上午10:36
 */
public class QDSslContextFactory {
    private static final String CLIENT_TRUST_PASSWORD = "214886428550884";//信任证书密码
    private static final String CLIENT_AGREEMENT = "TLS";//使用协议
    private static final String CLIENT_TRUST_MANAGER = "X509";
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";
    SSLContext sslContext = null;

    public SSLContext getSslSocket(Context context) {
        try {
            //取得SSL的SSLContext实例
            sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
            //取得TrustManagerFactory的X509密钥管理器实例
            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
            //取得BKS密库实例
            KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
//            InputStream is = context.getResources().openRawResource(R.raw.key);
//            try {
//                tks.load(is, CLIENT_TRUST_PASSWORD.toCharArray());
//            } finally {
//                is.close();
//            }
            //初始化密钥管理器
            trustManager.init(tks);
            //初始化SSLContext
            sslContext.init(null, trustManager.getTrustManagers(), null);
        } catch (Exception e) {
            Log.e("SslContextFactory", e.getMessage());
        }
        return sslContext;
    }
}
