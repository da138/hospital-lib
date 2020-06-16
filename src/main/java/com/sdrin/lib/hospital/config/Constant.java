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
package com.sdrin.lib.hospital.config;

/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/15 11:16 上午
 *
 * @author 胡树铭
 */
public class Constant {
    public static final String CHARSET = "UTF-8";
    /**
     * rsa api交互时，加密的类型
     */
    public static final String ENC_TYPE = "AES+RSA2";

    /**
     * 数字签名的技术。
     */
    public static final String HTTP_VERSION = "1.0";
    public static final String SIGN_TYPE = "SHA3+RSA2";
    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LOCAL_TIME_FORMAT = "H:m";


}
