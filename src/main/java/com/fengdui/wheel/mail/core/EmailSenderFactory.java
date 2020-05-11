package com.fengdui.wheel.mail.core;


import com.fengdui.wheel.mail.sender.HtmlSender;
import com.fengdui.wheel.mail.sender.MultiPartSender;

/**
 * Email发送对象工厂类, 根据要发送Email的类型返回具体的发送实现.
 */
public class EmailSenderFactory {

	/**
	 * 获取指定类型的邮件发送者。
	 * 
	 * @param emailType
	 *            类型
	 * @param config
	 *            SMTP配置信息
	 */
	public static EmailSender getSender(int emailType, SMTPConfig config) {
		EmailSender sender = null;

		switch (emailType) {
		case Email.SIMPLE_EMAIL:
		case Email.HTML_EMAIL: {
			sender = new HtmlSender(config);
			break;
		}
		case Email.MULTI_PART_EMAIL: {
			sender = new MultiPartSender(config);
			break;
		}
		default: {
			throw new EmailException(ErrorCode.UNSUPPORT_EMAIL_TYPE);
		}
		}

		return sender;
	}
}