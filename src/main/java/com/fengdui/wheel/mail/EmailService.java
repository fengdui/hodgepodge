package com.fengdui.wheel.mail;

import java.util.List;

import com.fengdui.wheel.mail.core.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

/**
 * 邮件发送工具
 */
public class EmailService {
	private String hostName;
	private int port;
	private String username;
	private String password;
	private String display;
	private String defaultTo;//默认收件人邮箱地址,用","分隔

	/** 发送邮件 */
	public void send(Email message, int emailType) {
		if (null == message)
			throw new EmailException(ErrorCode.MAIL_IS_NULL);
		if (StringUtils.isBlank(message.getDisplay()))
			message.setDisplay(display);
		if (StringUtils.isBlank(message.getFrom()))
			message.setFrom(username);
		message.setEmailType(emailType);

		SMTPConfig config = new SMTPConfig(hostName, port, username, password);
		EmailSender sender = EmailSenderFactory.getSender(emailType, config);

		sender.send(message);
	}
	
	/**
	 * 使用默认设置发送邮件
	 * @param subject 邮件主题
	 * @throws Exception 
	 */
	public void sendDefault(String subject, String content, int emailType) throws Exception{
		Email message = new Email();
		message.setSubject(subject);
		message.setFrom(username, display);
		if(StringUtils.isBlank(defaultTo)){
			throw new Exception("默认收件人邮箱地址为空");
		}
//		List<String> to = StrHelper.fetchStringToList(defaultTo, ",");
		List<String> to = Lists.newArrayList();
		message.setTo(to);
		message.setContent(content);
		send(message, emailType);
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getDefaultTo() {
		return defaultTo;
	}

	public void setDefaultTo(String defaultTo) {
		this.defaultTo = defaultTo;
	}
}