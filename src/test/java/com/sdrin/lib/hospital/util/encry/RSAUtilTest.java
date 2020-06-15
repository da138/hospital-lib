package com.sdrin.lib.hospital.util.encry;

import com.google.gson.Gson;
import com.sdrin.lib.hospital.domain.SHttpRequest;
import com.sdrin.lib.hospital.domain.SHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/15 5:11 下午
 *
 * @author 胡树铭
 */
class RSAUtilTest {
    private String plainText = "你好123";
    private Map<String, byte[]> keyMap = new HashMap<>();
    private byte[] publicKey = new byte[1];
    private byte[] privateKeyPkcs8 = new byte[1];

    @BeforeEach
    void init() {
        keyMap = RSAUtil.initKey();
        publicKey = keyMap.get(RSAUtil.PUBLIC_KEY);
        privateKeyPkcs8 = keyMap.get(RSAUtil.PRIVATE_KEY_PKCS8);
    }

    @Test
    void encryptAndDecrypt() {
        String encrypt = RSAUtil.encrypt(plainText, publicKey);
        System.out.println(encrypt);
        assertTrue(RSAUtil.decrypt(encrypt, privateKeyPkcs8).equals(plainText));
    }

    @Test
    void signWithSha3AndRsa256AndVerify() {
        String sign = RSAUtil.signWithSha3AndRsa256(privateKeyPkcs8, plainText);
        System.out.println(sign);
        assertTrue(RSAUtil.verifyWithSha3AndRsa256(publicKey, plainText, sign));
    }

    @Test
    void signHttpRequest() {
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("name", "name");
        bizContent.put("value", 1);
        Gson gson = new Gson();
        SHttpRequest request = new SHttpRequest("appId123", gson.toJson(bizContent), false);
        RSAUtil.sign(request, privateKeyPkcs8);
        System.out.println(request);
        assertTrue(RSAUtil.verify(request, publicKey));
    }

    @Test
    void signHttpResponse() {
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("name", "name");
        bizContent.put("value", 1);
        Gson gson = new Gson();
        SHttpResponse response = new SHttpResponse("10001", "err", gson.toJson(bizContent));
        RSAUtil.sign(response, privateKeyPkcs8);
        System.out.println(response);
        assertTrue(RSAUtil.verify(response, publicKey));
    }
}
