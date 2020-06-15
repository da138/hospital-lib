package com.sdrin.lib.hospital.util.encry;

import com.sdrin.lib.hospital.config.Constant;
import com.sdrin.lib.hospital.domain.SHttpRequest;
import com.sdrin.lib.hospital.domain.SHttpResponse;

/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/6/15 7:55 下午
 * 对报文内容进行摘要，这里是摘要的方法。
 *
 * @author 胡树铭
 */
public class DigitUtil {
    /**
     * 对request 内容进行摘要，规则是：
     * 取所有请求参数，不包括字节类型参数，如文件、字节流，剔除 sign 字段。
     * 剩下字段为：(appId、timestamp、letter、signType、encType、bizContent、version)，如果该段内容无需加密，则encType和letter为空。
     * 将筛选的参数按照第一个字符的键值 ASCII 码递增排序(字母升序排序)，
     * 如果遇到相同字符则按 照第二个字符的键值 ASCII 码递增排序，以此类推, 参考 http://simulate-his.sdrin.com/docs/index.html#_4_1_1_2_摘要
     *
     * @param request 被做摘要的参数
     * @return 返回摘要后的内容，string
     */
    public static String digit(SHttpRequest request) {
        // 按照字典排序。
        StringBuffer content = new StringBuffer("appId=")
                .append(request.getAppId())
                .append("&bizContent=")
                .append(request.getBizContent());

        if (request.getEncType() != null && request.getEncType().equalsIgnoreCase(Constant.ENC_TYPE)) {
            content.append("&encType=")
                    .append(Constant.ENC_TYPE)
                    .append("&letter=")
                    .append(request.getLetter());
        }
        content.append("&signType=")
                .append(Constant.SIGN_TYPE)
                .append("&timestamp=")
                .append(request.getTimestamp())
                .append("&version=")
                .append(request.getVersion());
        return content.toString();
    }

    /**
     * 对返回值进行摘要排序，并返回摘要值。
     *
     * @param response 返回值。
     * @return 排序后的摘要值
     */
    public static String digit(SHttpResponse response) {
        // 按照字典排序。
        StringBuffer content = new StringBuffer();
        if (response.getBizContent() != null)
            content.append("bizContent=").append(response.getBizContent());
        content.append("&code=").append(response.getCode());
        if (response.getLetter() != null)
            content.append("&letter=").append(response.getLetter());
        if (response.getMsg() != null)
            content.append("&msg=").append(response.getMsg());
        return content.toString();
    }
}
