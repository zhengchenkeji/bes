package com.zc.common.core.license;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.validation.constraints.NotNull;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 授权加密解密工具类，做成授权工具的话，主要就是用的这个类
 */
public class DESUtils {
    /**
     * 密钥
     */
    public static final String SECRET_KEY = "34{ty!?/";


    /**
     * 解密数据
     *
     * @param data 要解密的数据
     * @return 解密结果
     * @throws Exception
     */
    public static String decrypt(@NotNull String data) throws Exception {
        return new String(decrypt(data, SECRET_KEY), StandardCharsets.UTF_8);
    }

    /**
     * 解密数据
     *
     * @param data 要解密的数据
     * @param key  密钥
     * @return 解密结果
     * @throws Exception
     */
    public static byte[] decrypt(@NotNull String data, @NotNull String key) throws Exception {

        byte[] msgBytes = Hex.decodeHex(data.toCharArray());

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        return cipher.doFinal(msgBytes);
    }


    /**
     * 加密数据
     *
     * @param data 需要加密的数据
     * @return 加密结果
     * @throws Exception
     */
    public static String encrypt(@NotNull String data) throws Exception {
        return Hex.encodeHexString(encrypt(data, SECRET_KEY)).toUpperCase();
    }

    /**
     * 加密数据
     *
     * @param data 要加密的信息
     * @param key  密钥
     * @return 加密结果
     * @throws Exception
     */
    public static byte[] encrypt(@NotNull String data, @NotNull String key) throws Exception {

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

}