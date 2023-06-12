package com.zc.connect.util;

/**
 * @ClassName RadixUtil
 * @Description
 * @Author tjw
 * @Date 2021/7/6 10:00
 * @Version V1.0
 **/
public class RadixUtil {
    private RadixUtil() {

    }

    /**
     * 把一个十六进制的字符串数组，转化成byte数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hex2Byte(String[] hexString) {
        byte[] result = new byte[hexString.length];
        int count = 0;
        for (String s : hexString) {
            result[count] = ((byte) Integer.parseInt(s, 16));
            count++;
        }
        return result;
    }

    /**
     * 把一个 十六进制的字符串转化成byte数组
     *
     * @param
     * @return
     */
    public static byte[] hexToByte(String str) {
        String[] hexString = toStringArray(str);
        byte[] result = new byte[hexString.length];
        int count = 0;
        for (String s : hexString) {
            if (!s.equals("")) {
                result[count] = ((byte) Integer.parseInt(s, 16));
                count++;
            }
        }
        return result;
    }

    /**
     * 把一个十六进制的字符串，转化成String数组(2位)
     *
     * @param str
     * @return
     */
    public static String[] toStringArray(String str) {
        StringBuffer sb = new StringBuffer(str);
        int index;
        for (index = 2; index < sb.length(); index += 3) {
            sb.insert(index, ',');
        }
        return sb.toString().split(",");
    }
    /**
     * 把一个十六进制的字符串，转化成String数组(4位)
     *
     * @param str
     * @return
     */
    public static String[] toStringArrayFore(String str) {
        StringBuffer sb = new StringBuffer(str);
        int index;
        for (index = 4; index < sb.length(); index += 5) {
            sb.insert(index, ',');
        }
        return sb.toString().split(",");
    }

    /**
     * 把一个十六进制的字符串 高低位互换
     * @param str 以字节为单位的数字字符串(不包含0x和空格)
     * @return
     */
    public static String lowOrHighReverse(String str) {
        StringBuffer sb = new StringBuffer("");
        for (int i = str.length(); i > 0; i = i - 2) {
            sb.append(str.substring(i - 2, i));
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转换成十进制字符串
     *
     * @param hex
     * @return
     */
    public static String hexToDecimal(String hex) {
        if (hex.startsWith("0x")) {
            return String.valueOf(Integer.parseInt(hex.substring(2), 16));
        } else {
            return String.valueOf(Integer.parseInt(hex, 16));
        }
    }

    /**
     * 将byte转化为十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02x ", b));
        }
        return sb.toString().trim();
    }

    /**
     * 将byte转化为十六进制字符串 长度为1的在前面追加0
     *
     * @param
     * @return
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * 将int类型转换为16进制
     *
     * @param number
     * @param byteNumber
     * @return
     */
    public static String int2Hex(int number, int byteNumber) {
        String result = Integer.toHexString(number);
        if (result.length() == 1) {
            if (byteNumber == 2)
                return "00 0" + result;
            return "0" + result;
        }
        if (result.length() == 2) {
            if (byteNumber == 2)
                return "00 " + result;
            return result;
        }
        if (result.length() == 3) {
            if (byteNumber == 1)
                return result.substring(1);
            result = "0" + result;
            return result.substring(0, 2) + " " + result.substring(2);
        }
        if (result.length() == 4) {
            if (byteNumber == 1)
                return result.substring(2);
            return result.substring(0, 2) + " " + result.substring(2);
        }
        if (result.length() > 4) {
            if (byteNumber == 1)
                return result.substring(result.length() - 2);
            return result.substring(result.length() - 4);
        }

        return null;
    }

    /**
     * int类型转换为十六进制
     *
     * @param number
     * @return
     */
    public static String int2Hex(int number) {
        return int2Hex(number, 1);
    }

    /**
     * 将十六进制文本转化位ascii码
     *
     * @param
     * @return
     */

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }


}
