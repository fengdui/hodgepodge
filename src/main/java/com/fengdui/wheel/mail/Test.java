package com.fengdui.wheel.mail;

import com.fengdui.wheel.mail.core.Email;
import com.fengdui.wheel.mail.core.EmailSender;
import com.fengdui.wheel.mail.core.EmailSenderFactory;
import com.fengdui.wheel.mail.core.SMTPConfig;

public class Test {

	public static void main(String[] args) {
		SMTPConfig config = new SMTPConfig("smtp.ym.163.com",
				"www.com", "A654321~");
		Email message = new Email();
		message.setEmailType(Email.MULTI_PART_EMAIL);
		message.setSubject("Hello world");
		message.setFrom("fd", "wander");
		message.addTo("cengjingsea@163.com");
		message.addAttachment("http://www.baidu.com/img/baidu_jgylogo3.gif");
		message.addAttachment("D:\\.myeclipse.properties");
		message.setContent("Hello world");
		EmailSender sender = EmailSenderFactory.getSender(
				Email.MULTI_PART_EMAIL, config);
		sender.send(message);
	}
}
