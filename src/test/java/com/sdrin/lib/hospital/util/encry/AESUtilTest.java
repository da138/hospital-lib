package com.sdrin.lib.hospital.util.encry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/15 6:42 下午
 *
 * @author 胡树铭
 */
class AESUtilTest {
    @Test
    void encryptAndDecry() {
        String key = "123你好";
        String plainText = "上海石指abc12";

        String enctypt = AESUtil.encrypt(plainText, key);
        assertTrue(AESUtil.decryptTo(enctypt, key).equals(plainText));
    }

}
