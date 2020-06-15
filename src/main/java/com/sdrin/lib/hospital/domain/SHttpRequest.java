/*
 * Copyright (c) 2019 - 2020 上海石指（健康）科技有限公司. All Rights Reserved
 */

package com.sdrin.lib.hospital.domain;

import com.sdrin.lib.hospital.config.Constant;
import com.sdrin.lib.hospital.util.date.DateTimeUtil;

import java.time.LocalDateTime;

import static com.sdrin.lib.hospital.config.Constant.HTTP_VERSION;

/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/7 1:01 下午
 * <p>
 * 在进行和上海石指慧眼系统进行交互时，如果需要rsa签名和加密，则此为提交的request body，
 * 可以参考：http://simulate-his.sdrin.com/docs/index.html#_4_数字签名和数据加密
 *
 * @author 胡树铭
 */
public class SHttpRequest {
    /**
     * 连接对方系统的唯一账户id，由对方系统分配，
     */
    private String appId;
    /**
     * 具体的业务内容，json string 格式。
     */
    private String bizContent;
    /**
     * 签名类型，目前支持的是：SHA3+RSA2，参考：http://simulate-his.sdrin.com/docs/index.html#_4_1_1_1_签名流程
     */
    private String signType;
    /**
     * 根据SHA3+RSA2做的数字签名密文
     */
    private String sign;
    /**
     * 加密技术类型，默认是 AES+RSA2，是否加密由业务场景而定, 一般情况下，请求服务器是 {@code HTTPS} 则无需加密，
     * 否则涉及到敏感内容的均需加密：参考 http://simulate-his.sdrin.com/docs/index.html#_4_2_数据加密
     */
    private String encType;
    /**
     * 是上面数字信封加密后的保存的数字信封，先通过AES对称算法生成随机对称密钥，
     * 和利用AES对称算法对bizContent加密得到密文A,；再使用上海石指公钥和RSA2算法对"对称密钥"进行加密得到密文B，密文B就是数字信封。
     * 也就是这里的变量 letter
     */
    private String letter;
    /**
     * 当前时间点，也是时间戳。
     */
    private String timestamp;
    /**
     * 当前测版本。默认是 1.0
     */
    private String version;

    public SHttpRequest(String appId, String bizContent, boolean needEncrypt) {
        this.appId = appId;
        this.bizContent = bizContent;
        this.timestamp = DateTimeUtil.toString(LocalDateTime.now());
        this.version = HTTP_VERSION;
        if (needEncrypt)
            this.encType = Constant.ENC_TYPE;
        this.signType = Constant.SIGN_TYPE;
    }

    public void setBizContent(String bizContent) {
        this.bizContent = bizContent;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppId() {
        return appId;
    }

    public String getBizContent() {
        return bizContent;
    }

    public String getSignType() {
        return signType;
    }

    public String getSign() {
        return sign;
    }

    public String getEncType() {
        return encType;
    }

    public String getLetter() {
        return letter;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "appId='" + appId + '\'' +
                ", bizContent='" + bizContent + '\'' +
                ", signType='" + signType + '\'' +
                ", sign='" + sign + '\'' +
                ", encType='" + encType + '\'' +
                ", letter='" + letter + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}

