package com.zc.connect.util;

import com.ruoyi.common.utils.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @CkassName: StringUtil
 * @author Athena-YangChao
 * @Date: 2019/11/15 16:54
 * @Descruotuib: 字符串转换工具类
 * @Version: 1.0
 **/
public class StringUtil {


    /**
     * 字符串分割 - 2字节 分割
     */
    public static String[] StrToArrayTwo(String str){
        StringBuffer s1 = new StringBuffer(str);
        int index;
        for (index = 2; index < s1.length(); index += 3) {
            s1.insert(index, ',');
        }
        String[] array = s1.toString().split(",");

        return array;
    }

    /**
    * @Description: 16进制转10进制(无高低转换)
    * @author Athena-YangChao
    * @date 2019/11/15 17:11
    */
    public static String hexToDecimal(String hex) {
        if (hex.startsWith("0x")) {
            return String.valueOf(Integer.parseInt(hex.substring(2), 16));
        } else {
            return String.valueOf(Integer.parseInt(hex, 16));
        }
    }

    /**
     * 将16进制转换为2进制
     *
     * @param hexString
     * @return
     */
    public static String hex2Binary(String hexString) {
        if (StringUtils.isNull(hexString))
            return null;
        long result = Long.parseLong(hexString, 16);
        return Long.toBinaryString(result);
    }


    /**
     * 补位
     * @param data
     * @return
     */
    public static String add0forstr(String data) {
        while(data.length()<8) {
            StringBuffer sb = new StringBuffer();
            sb.append("0").append(data);// 左补0
            data = sb.toString();
        }
        return data;
    }

    /**
     * @Title:hexString2Bytes
     * @Description:16进制字符串转字节数组
     * @param src  16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    /**
     * @Description: 十六进制转字符串
     *
     */
    public static String hexStringToString(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 十进制转十六进制
     * @return
     */
    public static String intToHex(int Decimal){
        return Integer.toHexString(Decimal);
    }


    /**
     * hexStr 转 UnsignedLong
     * @param hexStr
     * @return
     */
    public static Long hexStrToUnsignedLong(String hexStr){
        if(StringUtils.isEmpty(hexStr)){
            return null;
        }
        return Long.parseUnsignedLong(hexStr,16);
    }

    /**
     * hexStr 转 Double
     * @param hexStr
     * @return
     */
    public static Double hexStrToDouble(String hexStr){
        if(StringUtils.isEmpty(hexStr)){
            return null;
        }
        long longBits = Long.valueOf(hexStr,16).longValue();
        return Double.longBitsToDouble(longBits);
    }

    /**
     * hexStr 转 Float
     * @param hexStr
     * @return
     */
    public static Float hexStrToFloat(String hexStr){
        if(StringUtils.isEmpty(hexStr)){
            return null;
        }
        Integer integerBits = Integer.valueOf(hexStr.trim(),16);
        return Float.intBitsToFloat(integerBits);
    }

    /**
     * hexStr 转 BigDecimal
     * @param hexStr
     * @return
     */
    public static BigDecimal hexStrToBigDecimal(String hexStr){
        if(StringUtils.isEmpty(hexStr)){
            return null;
        }
        BigInteger bigInteger = new BigInteger(hexStr, 16);
        return new BigDecimal(bigInteger);
    }

    /**
     * hexStr 转 Byte
     * @param hexStr
     * @return
     */
    public static Byte hexStrToByte(String hexStr){
        if(StringUtils.isEmpty(hexStr)){
            return null;
        }
        return Byte.parseByte(hexStr,16);
    }
    /**
     * hexStr 转 Integer
     * @param hexStr
     * @return
     */
    public static Integer hexStrToInteger(String hexStr){
        if(StringUtils.isEmpty(hexStr)){
            return null;
        }
        return Integer.parseInt(hexStr,16);
    }

    /**
     * hexStr 转 BigInteger
     * @param hexStr
     * @return
     */
    public static BigInteger hexStrToBigInteger(String hexStr){
        if(StringUtils.isEmpty(hexStr)){
            return null;
        }
        return new BigInteger(hexStr,16);
    }

    /**
     * 将十六进制的字符串转换成字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStrToByteArrs(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            return null;
        }

        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        int index = 0;

        byte[] bytes = new byte[len / 2];

        while (index < len) {
            String sub = hexString.substring(index, index + 2);
            bytes[index / 2] = (byte) Integer.parseInt(sub, 16);
            index += 2;
        }

        return bytes;
    }

    /**
     * 数组转换成十六进制字符串
     *
     * @param bArray
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2){
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

}
