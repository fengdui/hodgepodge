package com.fengdui.wheel.net;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class IpUtils {
	/**
	  * 获取本地主机上绑定的所有IP地址
	  * @return 本地主机上绑定的所有IP地址
	  */
	public static Set<String> getLocalIps() {
		Set<String> ips = new LinkedHashSet<String>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces != null && interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				while (addresses != null && addresses.hasMoreElements()) {
					InetAddress addresse = addresses.nextElement();
					String ip = addresse.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						ips.add(ip);
					}
				}
			}
		} catch (SocketException e) {
		}
		return ips;
	}
	
	//在linux下有问题
	public String getHostName() throws Exception{
		InetAddress address = InetAddress.getLocalHost();
		return address.getHostName();
	}

	/**
	 * 获取登录用户的IP地址
	 * @param request 请求
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "本地";
		}
		if (ip.split(",").length > 1) {
			ip = ip.split(",")[0];
		}
		return ip;
	}
	/**
	 * IP加密处理
	 * @param ip  需要进行处理的IP
	 * @return
	 */
	public static String hideIp(String ip) {
		if (StringUtils.isEmpty(ip)) {
			return "";
		}
		int pos = ip.lastIndexOf(".");
		if (pos == -1) {
			return ip;
		}
		ip = ip.substring(0, pos + 1);
		ip = ip + "*";
		return ip;
	}
	
	public static void main(String[] args) throws UnknownHostException {
		Set<String> ipSet = IpUtils.getLocalIps();
		boolean b = ipSet.contains("192.168.1.107");
		for(String s : ipSet){
			System.out.println(s);
		}
	}
}