/*
Copyright 2019-2020 上海石指健康科技有限公司

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.sdrin.lib.hospital.util.encry;


import org.bouncycastle.util.encoders.Hex;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static com.sdrin.lib.hospital.config.Constant.CHARSET;


/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/14 10:33 下午
 * <p>
 * 对称加密，解密，它比rsa速度更快，可以和rsa结合
 *
 * @author 胡树铭
 */
public class AESUtil {
    private static final String RNG_ALGORITHM = "SHA1PRNG";
    /**
     * 加密/解密算法名称
     */
    private static final String ALGORITHM = "AES";

    /**
     * 生成256位字节随机密钥
     *
     * @param key 自定义随机字符串，用于生成aes密钥。
     * @return 返回随机密钥
     */
    private static SecretKey getKey(String key) {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
            // 设置 密钥key的字节数组 作为安全随机数生成器的种子
            random.setSeed(key.getBytes(CHARSET));
            keyGen.init(256, random);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertSecretKeyToStr(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static byte[] encryptToBytes(String plain_text, String key) {
        SecretKey secKey = getKey(key);
        // 获取 AES 密码器
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            // 初始化密码器（加密模型）
            cipher.init(Cipher.ENCRYPT_MODE, secKey);
            // 加密数据, 返回密文
            return cipher.doFinal(plain_text.getBytes(CHARSET));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException |
                IllegalBlockSizeException | InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String plain_text, String key) {
        return Hex.toHexString(encryptToBytes(plain_text, key));
    }

    public static byte[] decryptToBytes(String cipherText, String key) {
        // 生成密钥对象
        SecretKey secKey = getKey(key);

        // 获取 AES 密码器
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            // 初始化密码器（解密模型）
            cipher.init(Cipher.DECRYPT_MODE, secKey);
            // 解密数据, 返回明文
            byte[] plainBytes = cipher.doFinal(Hex.decode(cipherText));
            return plainBytes;
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptTo(String cipherText, String key) {
        return new String(decryptToBytes(cipherText, key));
    }
}
