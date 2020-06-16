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

package com.sdrin.lib.hospital.domain;


/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/7 1:01 下午
 * <p>
 * 在进行和上海石指慧眼系统进行交互时，如果需要rsa签名和加密，则此为提交的request body，
 * 可以参考：http://simulate-his.sdrin.com/docs/index.html#_4_数字签名和数据加密
 *
 * @author 胡树铭
 */
public class SHttpResponse {
    /**
     * 返回码，详见：http://simulate-his.sdrin.com/docs/index.html#_6_api_公共错误码
     */
    private String code;
    /**
     * 返回的描述
     */
    private String msg;

    /**
     * 签名，使用request 的参数。签名类型
     */
    private String sign;

    /**
     * 具体的业务内容，json string 格式。
     */
    private String bizContent;

    /**
     * 是上面数字信封加密后的保存的数字信封，先通过AES/ESB对称算法生成128位随机对称密钥，
     * 和利用AES对称算法对bizContent加密得到密文A,；再使用上海石指公钥和RSA2算法对"对称密钥"进行加密得到密文B，密文B就是数字信封。
     * 也就是这里的变量 letter
     */
    private String letter;

    public SHttpResponse(String code, String msg, String bizContent) {
        this.code = code;
        this.msg = msg;
        this.bizContent = bizContent;
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

    public String getBizContent() {
        return bizContent;
    }

    public String getSign() {
        return sign;
    }

    public String getLetter() {
        return letter;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "SHttpResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", sign='" + sign + '\'' +
                ", bizContent='" + bizContent + '\'' +
                ", letter='" + letter + '\'' +
                '}';
    }
}

