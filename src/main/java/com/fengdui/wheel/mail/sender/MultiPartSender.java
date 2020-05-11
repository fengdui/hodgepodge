package com.fengdui.wheel.mail.sender;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.fengdui.wheel.mail.core.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 带附件的邮件发送者。
 */
public class MultiPartSender extends AbstractEmailSender {

	private static Logger LOGGER = LoggerFactory
			.getLogger(MultiPartSender.class);

	private MultiPartEmail multiPartEmail;

	public MultiPartSender(SMTPConfig config) {
		super(config);
		multiPartEmail = new MultiPartEmail();
		this.initMultiPartEmail(config);
	}

	@Override
	public boolean send(Email message) {
		this.email = message;
		check(this.email);

		try {
			multiPartEmail.setFrom(this.email.getFrom(),
					this.email.getDisplay());
			for (String to : this.email.getTo()) {
				multiPartEmail.addTo(to);
			}
			multiPartEmail.setSubject(email.getSubject());
			multiPartEmail.setCharset(email.getCharset());
			multiPartEmail.setMsg(email.getContent());
			this.attach(email.getAttachments());
			multiPartEmail.send();
		} catch (org.apache.commons.mail.EmailException e) {
			LOGGER.error(ErrorCode.SEND_MAIL_ERROR, e);
			throw new EmailException(ErrorCode.SEND_MAIL_ERROR, e);
		}
		return false;
	}

	/** 向邮件中添加附件。 */
	private void attach(List<String> attachmentFiles) {
		if (CollectionUtils.isNotEmpty(attachmentFiles)) {
			EmailAttachment attach = null;

			try {
				for (String file : attachmentFiles) {
					attach = getEmailAttachment(file);
					multiPartEmail.attach(attach);
				}
			} catch (Exception e) {
				throw new EmailException(ErrorCode.ADD_ATTACHMENT_ERROR, e);
			}
		}
	}

	/** 设置附件地址 */
	private EmailAttachment getEmailAttachment(String file) {
		EmailAttachment attach = new EmailAttachment();
		if (file.indexOf("http") == -1) {
			attach.setPath(file);
		} else {
			try {
				attach.setURL(new URL(file));
			} catch (MalformedURLException e) {
				throw new EmailException(ErrorCode.ADD_URL_ATTACHMENT_ERROR, e);
			}
		}

		return attach;
	}

	/** 初始化附件邮件发送者 */
	private void initMultiPartEmail(SMTPConfig config) {
		String host = config.getHostName();
		if (StringUtils.isBlank(host)) {
			throw new EmailException(ErrorCode.SMTP_HOST_NULL);
		}
		multiPartEmail.setHostName(host);
		multiPartEmail.setSmtpPort(config.getSmtpPort());
		multiPartEmail.setAuthentication(config.getUsername(),
				config.getPassword());
	}
}