package com.gavin.giframe.authcode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class GIAESOperator {
    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    public static String sKey = "";
    private String ivParameter = "ivparametersuncn";
    private static GIAESOperator instance = null;

    private GIAESOperator() {

    }

    public static GIAESOperator getInstance() {
        if (instance == null)
            instance = new GIAESOperator();
        return instance;
    }

//	public static String Encrypt(String encData, String secretKey, String vector) throws Exception {
//		if (secretKey == null) {
//			return null;
//		}
//		if (secretKey.length() != 16) {
//			return null;
//		}
//		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		byte[] raw = secretKey.getBytes();
//		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//		IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
//		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//		byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
//		return new MyBase64().encodeBytes(encrypted);// 此处使用BASE64做转码。
//	}

    // 加密
    public String encrypt(String sSrc) {
        try {
            if (sSrc != null) {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                byte[] raw = sKey.getBytes();
                SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
                return new MyBase64().encodeBytes(encrypted);// 此处使用BASE64做转码。
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解密
    public String decrypt(String sSrc) {
        if (sSrc != null && !sSrc.equals("")) {
            try {
                byte[] raw = sKey.getBytes("ASCII");
                SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                byte[] encrypted = new MyBase64().decode(sSrc);// 先用base64解密
                byte[] original = cipher.doFinal(encrypted);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

//	public String decrypt(String sSrc, String key, String ivs) throws Exception {
//		try {
//			byte[] raw = key.getBytes("ASCII");
//			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
//			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//			byte[] encrypted1 = new MyBase64().decode(sSrc);// 先用base64解密
//			byte[] original = cipher.doFinal(encrypted1);
//			String originalString = new String(original, "utf-8");
//			return originalString;
//		} catch (Exception ex) {
//			return null;
//		}
//	}

//	public static String encodeBytes(byte[] bytes) {
//		StringBuffer strBuf = new StringBuffer();
//
//		for (int i = 0; i < bytes.length; i++) {
//			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
//			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
//		}
//
//		return strBuf.toString();
//	}

//    public static void main(String[] args) throws Exception {
//        // 需要加密的字串
//        String cSrc = "安徽商信政通信息技术股份有限公司";
//
//        // 加密
//        long lStart = System.currentTimeMillis();
//        String enString = GIAESOperator.getInstance().encrypt(cSrc);
//        System.out.println("加密后的字串是：" + enString);
//
//        long lUseTime = System.currentTimeMillis() - lStart;
//        System.out.println("加密耗时：" + lUseTime + "毫秒");
//        // 解密
//        lStart = System.currentTimeMillis();
//        String DeString = GIAESOperator.getInstance().decrypt(enString);
//        System.out.println("解密后的字串是：" + DeString);
//        lUseTime = System.currentTimeMillis() - lStart;
//        System.out.println("解密耗时：" + lUseTime + "毫秒");
//    }

}