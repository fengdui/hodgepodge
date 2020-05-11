package com.fengdui.wheel.net;


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
	
	public static void main(String[] args) throws UnknownHostException {
		Set<String> ipSet = IpUtils.getLocalIps();
		boolean b = ipSet.contains("192.168.1.107");
		for(String s : ipSet){
			System.out.println(s);
		}
	}
}