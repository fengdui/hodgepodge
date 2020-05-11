package com.fengdui.wheel.mail.sender;

import com.fengdui.wheel.mail.core.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HTML 格式的邮件发送者
 */
public class HtmlSender extends AbstractEmailSender {

	private static Logger LOGGER = LoggerFactory.getLogger(HtmlSender.class);

	private HtmlEmail htmlEmail;

	public HtmlSender(SMTPConfig config) {
		super(config);
		htmlEmail = new HtmlEmail();
		initHtmlEmail(config);
	}

	@Override
	public boolean send(Email message) {
		email = message;
		check(email);

		try {
			htmlEmail.setFrom(this.email.getFrom(), email.getDisplay());
			for (String to : email.getTo()) {
				htmlEmail.addTo(to);
			}
			htmlEmail.setSubject(email.getSubject());
			htmlEmail.setCharset(email.getCharset());
			htmlEmail.setHtmlMsg(email.getContent());

			// 完成发送
			htmlEmail.send();
		} catch (Exception e) {
			LOGGER.error(ErrorCode.SEND_MAIL_ERROR, e);
			throw new EmailException(ErrorCode.SEND_MAIL_ERROR, e);
		}
		return false;
	}

	/** 初始化HTML邮件发送者 */
	private void initHtmlEmail(SMTPConfig config) {
		String host = config.getHostName();
		if (StringUtils.isBlank(host)) {
			throw new EmailException(ErrorCode.SMTP_HOST_NULL);
		}

		htmlEmail.setHostName(host);
		htmlEmail.setSmtpPort(config.getSmtpPort());
		htmlEmail.setAuthentication(config.getUsername(), config.getPassword());
	}
}