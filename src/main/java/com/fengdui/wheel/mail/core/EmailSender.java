package com.fengdui.wheel.mail.core;

/**
 * 邮件发送者接口，具体实现者需要实现该接口。
 */
public interface EmailSender {
	/** 发送邮件接口 */
	boolean send(Email message);
}