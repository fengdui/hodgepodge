package com.fengdui.wheel.mail;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.mail.EmailAttachment;

/**
 * commons-email工具包
 *
 * @since 2020/5/21 4:15 下午
 */
public class CommonEmailUtils {

    /**
     * 设置附件地址
     */
    private EmailAttachment getEmailAttachment(byte[] bytes) throws DecoderException {
        EmailAttachment attach = new EmailAttachment();
        attach.setURL(new ByteURL().getResource("test", Hex.decodeHex("0xe04fd020ea3a6910a2d808002b30309d")));
        return attach;
    }
}
