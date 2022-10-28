package com.fengdui.wheel.mail;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.mail.EmailAttachment;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * commons-email工具包
 *
 * @since 2020/5/21 4:15 下午
 */
public class CommonEmailUtils {
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    /**
     * 设置附件地址
     */
    private EmailAttachment getEmailAttachment(byte[] bytes) throws DecoderException {
        EmailAttachment attach = new EmailAttachment();
        attach.setURL(new ByteURL().getResource("test", Hex.decodeHex("0xe04fd020ea3a6910a2d808002b30309d")));
        return attach;
    }

    /**
     * 短信模板填充
     * @param contentParams
     * @param tempContent
     * @return
     */
    public static  String getContent(Map<String, Object> contentParams , String tempContent) {
        // 替换内容
        AtomicReference<String> content = new AtomicReference<>(tempContent);
        if (null != contentParams) {
            contentParams.forEach((k, v) -> content.set(StringUtils.replace(content.get(), CommonEmailUtils.DEFAULT_PLACEHOLDER_PREFIX + k + CommonEmailUtils.DEFAULT_PLACEHOLDER_SUFFIX, convertString(v))));
        }

        if (!StringUtils.isEmpty(content.get())) {
            while (content.get().contains(CommonEmailUtils.DEFAULT_PLACEHOLDER_PREFIX)) {
                int startIndex = content.get().indexOf(CommonEmailUtils.DEFAULT_PLACEHOLDER_PREFIX);
                int endIndex = content.get().indexOf(CommonEmailUtils.DEFAULT_PLACEHOLDER_SUFFIX);
                String param = content.get().substring(startIndex, endIndex + 1);
                content.set(StringUtils.replace(content.get(), param, ""));
            }
        }
        return content.get();
    }
    private static  String convertString(Object object) {
        String result;
        if (object instanceof String) {
            result = (String) object;
        } else {
            result = JSON.toJSONString(object);
        }
        result = result.replace("\"", "\\\"");
        return result;
    }
}
