package com.fengdui.wheel.mail.core;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 抽象的邮件发送者。具体的HTML、带附件的发送者需继承此抽象类。
 */
public abstract class AbstractEmailSender implements EmailSender {
	protected SMTPConfig config;
	protected Email email;

	public AbstractEmailSender(SMTPConfig config) {
		super();
		this.config = config;
	}

	public abstract boolean send(Email message);

	/** 检测邮件信息是否合格 */
	protected void check(Email email) {
		if (email == null) {
			throw new RuntimeException(ErrorCode.MAIL_IS_NULL);
		}
		if (StringUtils.isEmpty(email.getFrom())) {
			throw new RuntimeException(ErrorCode.MAIL_FROM_ADDR_EMPTY);
		}
		if (CollectionUtils.isEmpty(email.getTo())) {
			throw new RuntimeException(ErrorCode.MAIL_TO_ADDR_EMPTY);
		}
	}
}