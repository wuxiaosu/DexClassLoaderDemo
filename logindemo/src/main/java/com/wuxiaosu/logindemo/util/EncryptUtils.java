package com.wuxiaosu.logindemo.util;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/**
 * Created by su on 2017/6/3.
 */

public class EncryptUtils {
    private final static String DES = "DES";
    private static String key = "88888888";


    /**
     * 加密
     */
    public static String encrypt(String data) {
        byte[] bt = new byte[0];
        try {
            bt = encrypt(data.getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bt, Base64.DEFAULT);
    }

    /**
     * 解密
     */
    public static String decrypt(String data) {
        if (data == null)
            return null;
        byte[] bt = new byte[0];
        try {
            bt = decrypt(Base64.decode(data, Base64.DEFAULT), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(bt);
    }

    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }


    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }
}
