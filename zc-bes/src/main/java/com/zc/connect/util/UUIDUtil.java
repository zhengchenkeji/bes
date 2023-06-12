package com.zc.connect.util;

import com.ruoyi.common.utils.uuid.UUID;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 10:17 2023/3/25
 * @Modified By:
 */
public class UUIDUtil {


    public static int generateNiceString() throws NoSuchAlgorithmException {
        // 创建 SecureRandom 对象，并设置加密算法
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        return random.nextInt(65535);
    }


}
