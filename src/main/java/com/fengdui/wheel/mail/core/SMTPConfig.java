package com.fengdui.wheel.mail.core;

/**
 * SMTP 配置对象。
 */
public class SMTPConfig {
	private static final long serialVersionUID = 8615325201966868745L;

	/** 默认SMTP服务器端口 */
	public static final int DEFAULT_SMTP_PORT = 25;

	private String hostName;// SMTP服务器
	private int smtpPort = DEFAULT_SMTP_PORT;// SMTP服务端口: 默认25
	private String username;// SMTP服务器验证用户名
	private String password;// SMTP服务器验证密码

	public SMTPConfig() {

	}

	/**
	 * 构造函数。
	 * 
	 * @param hostName
	 *            SMTP服务器
	 * @param username
	 *            SMTP服务器验证用户名
	 * @param password
	 *            SMTP服务器验证密码
	 */
	public SMTPConfig(String hostName, String username, String password) {
		this(hostName, DEFAULT_SMTP_PORT, username, password);
	}

	/**
	 * 构造函数。
	 * 
	 * @param hostName
	 *            SMTP服务器
	 * @param smtpPort
	 *            SMTP服务端口
	 * @param username
	 *            SMTP服务器验证用户名
	 * @param password
	 *            SMTP服务器验证密码
	 */
	public SMTPConfig(String hostName, int smtpPort, String username,
			String password) {
		this.hostName = hostName;
		this.smtpPort = smtpPort;
		this.username = username;
		this.password = password;
	}

	/**
	 * toString Method
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SmtpInfo{").append(" hostName=").append(hostName)
				.append(", smtpPort=").append(smtpPort).append(", username=")
				.append(username).append(", password=").append(password)
				.append(" }");

		return sb.toString();
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
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
}