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

import com.sdrin.lib.hospital.domain.SHttpRequest;
import com.sdrin.lib.hospital.domain.SHttpResponse;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import static com.sdrin.lib.hospital.config.Constant.CHARSET;


/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/7 6:35 下午
 *
 * @author 胡树铭
 */
public class RSAUtil {
    // MAX_DECRYPT_BLOCK应等于密钥长度/8（1byte=8bit），所以当密钥位数为2048时，最大解密长度应为256.
    //密钥算法
    public static final String ALGORITHM_RSA = "RSA";
    //RSA 签名算法
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PRIVATE_KEY_PKCS8 = "privateKeyPkcs8";
    public static final int ALGORITHM_RSA_PRIVATE_KEY_LENGTH = 2048;
    public static final String PUBLIC_KEY_DESCRIPTION = "PUBLIC KEY";
    public static final String PRIVATE_KEY_DESCRIPTION = "RSA PRIVATE KEY";// pkcs1
    public static final String PRIVATE_KEY_DESCRIPTION_PKCS8 = "PRIVATE KEY";
    public static final String PUBLIC_KEY_FILENAME = "rsa_public_key.pem";
    public static final String PRIVATE_KEY_FILENAME = "rsa_private_key.pem";// pkcs1
    public static final String PRIVATE_KEY_FILENAME_PKCS8 = "rsa_public_key_pkcs8.pem";


    /**
     * 产生 rsa key, 这里的私钥是，pkcs1 格式，返回的是字节形，这样便于存储在数据库，它比string格式好。
     *
     * @return 返回私有和公有的 rsa key
     */
    public static Map<String, byte[]> initKey() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + ALGORITHM_RSA + "]");
        }
        kpg.initialize(ALGORITHM_RSA_PRIVATE_KEY_LENGTH);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();
        Map<String, byte[]> keyPairMap = new HashMap<>();
        keyPairMap.put(PUBLIC_KEY, publicKey.getEncoded());
        keyPairMap.put(PRIVATE_KEY_PKCS8, privateKey.getEncoded());
        return keyPairMap;
    }


    /**
     * 从数据库里取出 公钥字节，对文本进行加密
     *
     * @param plain_text 加密前的字符串
     * @param publicKey  加密的key，字节
     * @return 返回加密后的string。 base64 格式。
     */
    public static String encrypt(String plain_text, byte[] publicKey) {
        //base64编码的公钥
        try {
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(ALGORITHM_RSA).generatePublic(new X509EncodedKeySpec(publicKey));
            //RSA加密
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return org.apache.commons.codec.binary.Base64.encodeBase64String(cipher.doFinal(plain_text.getBytes(CHARSET)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * rsa2 私钥解密，且是通过私钥的字节，pccs8 格式私钥，编码格式为 utf-8
     *
     * @param encryptStr      加密后的字符串， 密文
     * @param pkcs8PrivateKey 私钥，pkcs8。字节
     * @return 返回加密前的string
     */
    public static String decrypt(String encryptStr, byte[] pkcs8PrivateKey) {
        try {
            //64位解码加密后的字符串
            byte[] inputByte = org.apache.commons.codec.binary.Base64.decodeBase64(encryptStr.getBytes(CHARSET));
            RSAPrivateKey privateKey = (RSAPrivateKey) restorePkcs8PrivateKey(pkcs8PrivateKey);
            //RSA解密
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(inputByte));
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException
                | BadPaddingException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字节形的公钥，转为java对象
     *
     * @param keyBytes 字节形的公钥
     * @return 转为java对象
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将string格式的 公钥，转为java对象
     *
     * @param publicKeyStr string格式公钥
     * @return 返回java对象
     */
    public static PublicKey restorePublicKey(String publicKeyStr) {
        byte[] inputByte = new byte[0];
        try {
            inputByte = org.apache.commons.codec.binary.Base64.decodeBase64(publicKeyStr.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return restorePublicKey(inputByte);
    }

    /**
     * 将字节形的 pkcs8 私钥转为java对象
     *
     * @param pkcs8privateKeyBytes 字节形
     * @return 转为java对象，私钥
     */
    public static PrivateKey restorePkcs8PrivateKey(byte[] pkcs8privateKeyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(pkcs8privateKeyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey privateKey = factory
                    .generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将PKCS8EncodedKeySpec java 特有的私有key string 格式转为PrivateKey
     *
     * @param pkcs8PrivateKeyStr pkcs8 格式的java特有的私有key string
     * @return 转为java 私有key 对象
     */
    public static PrivateKey restorePkcs8PrivateKey(String pkcs8PrivateKeyStr) {
        byte[] inputByte = new byte[0];
        try {
            inputByte = org.apache.commons.codec.binary.Base64.decodeBase64(pkcs8PrivateKeyStr.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return restorePkcs8PrivateKey(inputByte);
    }

    /**
     * 将公钥/私钥/pkcs8 转为字节形，适用于所有的 key，适用于his传递pem格式公钥给上海石指，前台解析出内容string，传递给服务器，保存。
     *
     * @param keyStr key 的string 格式，utf-8
     * @return 字节形
     */
    public static byte[] convertKeyStrToBytes(String keyStr) {
        try {
            return org.apache.commons.codec.binary.Base64.decodeBase64(keyStr.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 签名，使用 SHA256withRSA , 可以参考：
     *
     * @param privateKey 使用私钥签名,字节，
     * @param plain_text 签名前的纯文本
     * @return 签名后的数据，base64 格式。
     * @see <a href="https://8gwifi.org/RSAFunctionality?rsasignverifyfunctions=rsasignverifyfunctions&keysize=2048">测试链接</a>
     */
    public static String signWithRsa256(byte[] privateKey, String plain_text) {
        Signature privateSignature;
        try {
            privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            privateSignature.initSign(restorePkcs8PrivateKey(privateKey));
            privateSignature.update(plain_text.getBytes(CHARSET));
            byte[] signedData = privateSignature.sign();
            return org.apache.commons.codec.binary.Base64.encodeBase64String(signedData);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证签名，使用 utf-8 编码, 可以查看:
     *
     * @param plain_text 原始字符串，签名之前的字符串
     * @param signedData 签名后的字符串，默认是：SHA256withRSA 签名
     * @param publicKey  公钥，发送方拥有的私钥匹配的公钥，不要混淆了。
     * @return 返回验证签名的结果。
     * @see <a href="https://8gwifi.org/RSAFunctionality?rsasignverifyfunctions=rsasignverifyfunctions&keysize=2048">测试链接</a>
     * 验证，注意上述网站没有使用 utf-8 编码，所以验证时，不要使用中文，但是正式环境下可以使用中文。
     */
    public static boolean verifyWithRsa256(byte[] publicKey, String plain_text, String signedData) {
        if (plain_text == null || signedData == null || publicKey == null) {
            return false;
        }
        try {
            Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            sign.initVerify(restorePublicKey(publicKey));
            sign.update(plain_text.getBytes(CHARSET));
            return sign.verify(org.apache.commons.codec.binary.Base64.decodeBase64(signedData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 将文本转为base 64 string
     *
     * @param plain_text 纯文本
     * @return 返回base64 格式string
     */
    public static String sha3_256(String plain_text) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashBytes = digest256.digest(
                plain_text.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(hashBytes));
    }

    /**
     * 先进行 sha3-256 做摘要，再进行私钥加密。这就是数字签名
     *
     * @param privateKey 私钥,pkcs8 格式。
     * @param plain_text 纯文本，数字签名前的对象
     * @return 数字签名后的对象
     */
    public static String signWithSha3AndRsa256(byte[] privateKey, String plain_text) {
        Signature privateSignature;
        try {
            privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            privateSignature.initSign(restorePkcs8PrivateKey(privateKey));
            // sha3 - 256
            Keccak.Digest256 digest256 = new Keccak.Digest256();
            byte[] hashBytes = digest256.digest(plain_text.getBytes(StandardCharsets.UTF_8));

            privateSignature.update(hashBytes);
            byte[] signedData = privateSignature.sign();
            return org.apache.commons.codec.binary.Base64.encodeBase64String(signedData);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 验证签名 ， 使用 utf-8 编码, 与上面的方法{@link #signWithSha3AndRsa256(byte[], String)} 对应。
     * 先sha3签名，再rsa加密。验证签名的顺序：
     *
     * @param plain_text 原始字符串，签名之前的字符串
     * @param signedData 签名后的字符串，默认是：SHA256withRSA 签名
     * @param publicKey  公钥，发送方拥有的私钥匹配的公钥，不要混淆了。
     * @return 返回验证签名的结果。
     */
    public static boolean verifyWithSha3AndRsa256(byte[] publicKey, String plain_text, String signedData) {
        if (plain_text == null || signedData == null || publicKey == null) {
            return false;
        }
        try {
            // sha3 - 256
            Keccak.Digest256 digest256 = new Keccak.Digest256();
            byte[] hashBytes = digest256.digest(plain_text.getBytes(StandardCharsets.UTF_8));

            Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            sign.initVerify(restorePublicKey(publicKey));
            sign.update(hashBytes);
            return sign.verify(org.apache.commons.codec.binary.Base64.decodeBase64(signedData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将pkcs8 格式私钥，转为pkcs1 格式，使得非java程序也能用, 已经验证过。用于前台显示。
     *
     * @param pkcs8PrivateKey java 生成的pccs8 格式的私钥
     * @return 转为base64 格式pkcs1 string
     */
    public static String convertPkcs8ToPkcs1(byte[] pkcs8PrivateKey) {
        PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(pkcs8PrivateKey);
        ASN1Encodable encodable;
        try {
            encodable = pkInfo.parsePrivateKey();
            ASN1Primitive primitive = encodable.toASN1Primitive();
            byte[] privateKeyPKCS1 = primitive.getEncoded();
            return org.apache.commons.codec.binary.Base64.encodeBase64String(privateKeyPKCS1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将公钥、私钥，pkcs8 格式key，转为string格式，便于前台显示。
     *
     * @param key 3种key
     * @return 返回base64编码后的string
     */
    public static String convertKeyByteToStr(byte[] key) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(key);
    }


    /**
     * 对请求body进行数字签名，这里只是数字签名，并没有加密。
     *
     * @param request    被数字签名前的请求body
     * @param privateKey 私钥
     */
    public static void sign(SHttpRequest request, byte[] privateKey) {
        // 此数字签名后的密文。
        String signed = signWithSha3AndRsa256(privateKey, DigitUtil.digit(request));
        request.setSign(signed);
    }

    /**
     * 验证发送方发送的内容，验证传输的内容有没有被修改，验证发送方是不是 曾经发给己方公钥的一方，所以这里使用的是发送方的公钥。
     *
     * @param request   请求内容，
     * @param publicKey 发送方共享的公钥，非己方生成的公钥
     * @return 如果验证通过，却是是发送方，则返回true，否则false
     */
    public static boolean verify(SHttpRequest request, byte[] publicKey) {
        return verifyWithSha3AndRsa256(publicKey, DigitUtil.digit(request), request.getSign());
    }

    /**
     * 对请求body进行数字签名，这里只是数字签名，并没有加密。
     *
     * @param response   被数字签名前的请求body
     * @param privateKey 私钥
     */
    public static void sign(SHttpResponse response, byte[] privateKey) {
        // 此数字签名后的密文。
        String signed = signWithSha3AndRsa256(privateKey, DigitUtil.digit(response));
        response.setSign(signed);
    }

    /**
     * 验证发送方返回的内容，验证传输的内容有没有被修改，验证发送方是不是 曾经发给己方公钥的一方，所以这里使用的是发送方的公钥。
     *
     * @param response  请求返回内容，
     * @param publicKey 发送方共享的公钥，非己方生成的公钥
     * @return 如果验证通过，却是是发送方，则返回true，否则false
     */
    public static boolean verify(SHttpResponse response, byte[] publicKey) {
        return verifyWithSha3AndRsa256(publicKey, DigitUtil.digit(response), response.getSign());
    }

    /**
     * 将rsa key 写入临时文件，便于服务器将文件返回给浏览器前台。
     *
     * @param key         rsa key，包含3种key，公钥，私钥，pkcs8
     * @param description 描述。
     * @param path        保存的本地目录
     * @return 返回文件的路径。
     */
    private static Path writePemFile(Key key, String description, Path path) {
        PemObject pemObject = new PemObject(description, key.getEncoded());

        try {
            if (!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path));
            PemWriter pemWriter = new PemWriter(outputStreamWriter);
            pemWriter.writeObject(pemObject);
            pemWriter.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
