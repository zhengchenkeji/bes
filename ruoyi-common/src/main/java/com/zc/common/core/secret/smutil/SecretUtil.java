package com.zc.common.core.secret.smutil;

import com.zc.common.core.secret.sm2.SM2EncDecUtils;
import com.zc.common.core.secret.sm2.SM2SignVO;
import com.zc.common.core.secret.sm2.SM2SignVerUtils;
import com.zc.common.core.secret.sm2.Sm2KeyVo;
import com.zc.common.core.secret.sm4.SM4Utils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;

import static com.zc.common.core.secret.smutil.Util.hash;

/**
 * @author Athena-gongfanfei
 * @Date 2021/09/16
 * @description 国密算法 SM2&SM3&SM4 加解密工具类
 */
public class SecretUtil {

    //SM2公钥编码格式
    //HardPubKey:3059301306072A8648CE3D020106082A811CCF5501822D034200+X+Y
    //SM2加密 密文区别:软加密多了04
    //SoftPubKey:04+X+Y
    //public static final String SM2PubHardKeyHead = "3059301306072A8648CE3D020106082A811CCF5501822D034200";

    //SM2加密机签名编码格式
    //HardSign:R+S
    //public static final String SM2PubHardKeyHead="3059301306072A8648CE3D020106082A811CCF5501822D034200";
    //private final String SM4_CBC_IV="";
    //private final String SM2="";

    /**
     * SM2-产生非对称秘钥
     * @return
     * @throws IOException
     */
    public static Sm2KeyVo generateSM2Key() throws IOException {
        Sm2KeyVo sm2KeyVo = SM2EncDecUtils.generateKeyPair();
        return sm2KeyVo;
    }

    /**
     * SM2-公钥加密
     * @param pubKey 公钥
     * @param src 需加密的明文
     * @return
     * @throws IOException
     */
    public static String SM2Enc(String pubKey, String src) throws IOException {
        String encrypt = SM2EncDecUtils.encrypt(Util.hexStringToBytes(pubKey), src.getBytes());
        //删除04
        encrypt=encrypt.substring(2,encrypt.length());
        return encrypt;
    }

    /**
     * SM2-私钥解密
     * @param priKey 私钥
     * @param encryptedData 已公钥加密的SM2密文
     * @return
     * @throws IOException
     */
    public static String SM2Dec(String priKey, String encryptedData) throws IOException {
        //填充04
        encryptedData="04"+encryptedData;
        byte[] decrypt = SM2EncDecUtils.decrypt(Util.hexStringToBytes(priKey), Util.hexStringToBytes(encryptedData));
        return new String(decrypt);
    }

    /**
     * SM2公钥soft和Hard转换
     * @param softKey
     * @return
     */
    /**
     * SM2公钥soft和Hard转换
     * @param pubKey 软密钥文
     * @param softKey  "04"
     * @return
     */
    public static String SM2PubKeySoftToHard(String pubKey, String softKey) {
        return pubKey + softKey;
    }

    /**
     * SM2公钥soft和Hard转换
     * @param hardKey 硬密钥文
     * @return
     */
    public static String SM2PubKeyHardToSoft(String hardKey) {
        return hardKey.replaceFirst("04", "");
    }

    /**
     * SM2-私钥签名
     * @param priKey 私钥
     * @param sourceData 原串必须是hex!!!!因为是直接用于计算签名的,可能是SM3串,也可能是普通串转Hex
     * @return
     * @throws Exception
     */
    public static SM2SignVO genSM2Signature(String priKey, String sourceData) throws Exception {
        SM2SignVO sign = SM2SignVerUtils.Sign2SM2(Util.hexToByte(priKey), Util.hexToByte(sourceData));
        return sign;
    }

    /**
     * SM2-公钥验签
     * @param pubKey 公钥
     * @param sourceData  明文hex字符串，参数二:原串必须是hex!!!!因为是直接用于计算签名的,可能是SM3串,也可能是普通串转Hex
     * @param hardSign 已加密字符串
     * @return
     */
    public static boolean verifySM2Signature(String pubKey, String sourceData, String hardSign) {
        SM2SignVO verify = SM2SignVerUtils.VerifySignSM2(Util.hexStringToBytes(pubKey), Util.hexToByte(sourceData), Util.hexToByte(hardSign));
        return verify.orVerify();
    }

    /**
     * SM2签名Hard转soft
     * @param hardSign hard加密签名
     * @return
     */
    public static String SM2SignHardToSoft(String hardSign) {
        byte[] bytes = Util.hexToByte(hardSign);
        byte[] r = new byte[bytes.length / 2];
        byte[] s = new byte[bytes.length / 2];
        System.arraycopy(bytes, 0, r, 0, bytes.length / 2);
        System.arraycopy(bytes, bytes.length / 2, s, 0, bytes.length / 2);
        ASN1Integer d_r = new ASN1Integer(Util.byteConvertInteger(r));
        ASN1Integer d_s = new ASN1Integer(Util.byteConvertInteger(s));
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(d_r);
        v2.add(d_s);
        DERSequence sign = new DERSequence(v2);

        String result = null;
        try {
            result = Util.byteToHex(sign.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * SM3摘要计算
     * @param src
     * @return
     */
    public static String generateSM3HASH(String src) {
        byte[] md = new byte[32];
        byte[] msg1 = src.getBytes();
        SM3Digest sm3 = new SM3Digest();
        sm3.update(msg1, 0, msg1.length);
        sm3.doFinal(md, 0);
        String s = new String(Hex.encode(md));
        return s.toUpperCase();
    }
    /**
     * SM3判断源数据与加密数据是否一致
     * @param srcStr       原字符串
     * @param sm3HexString 16进制字符串
     * @return 校验结果
     * @explain 通过验证原数组和生成的hash数组是否为同一数组，验证2者是否为同一数据
     */
    public static boolean verifySM3(String srcStr, String sm3HexString) {
        boolean flag = false;
        try {
            byte[] srcData = srcStr.getBytes("UTF-8");
            byte[] sm3Hash = ByteUtils.fromHexString(sm3HexString);
            byte[] newHash = hash(srcData);
            if (Arrays.equals(newHash, sm3Hash)) {
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * SM4产生对称秘钥
     * @return
     */
    public static String generateSM4Key() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * SM4-对称秘钥加密(CBC)
     * @param key
     * @param text
     * @return
     */
    public static String SM4EncForCBC(String key,String text) {
        SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = key;
        sm4.hexString = true;
        sm4.iv = "31313131313131313131313131313131";
        String cipherText = sm4.encryptData_CBC(text);
        return cipherText;
    }

    /**
     * SM4-对称秘钥解密(CBC)
     * @param key
     * @param text
     * @return
     */
    public static String SM4DecForCBC(String key,String text) {
        SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = key;
        sm4.hexString = true;
        sm4.iv = "31313131313131313131313131313131";
        String plainText = sm4.decryptData_CBC(text);
        return plainText;
    }

    /**
     * SM4-对称秘钥加密(ECB)
     * @param key
     * @param text
     * @return
     */
    public static String SM4EncForECB(String key,String text) {
        SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = key;
        sm4.hexString = true;
        String cipherText = sm4.encryptData_ECB(text);
        return cipherText;
    }

    /**
     * SM4-对称秘钥解密(ECB)
     * @param key
     * @param text
     * @return
     */
    public static String SM4DecForECB(String key,String text) {
        SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = key;
        sm4.hexString = true;
        String plainText = sm4.decryptData_ECB(text);
        return plainText;
    }
}
