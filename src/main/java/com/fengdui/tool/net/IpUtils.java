package com.fengdui.tool.net;


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

	public static InetAddress getLocalHostExactAddress() {
		try {
			InetAddress candidateAddress = null;

			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface iface = networkInterfaces.nextElement();
				// 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
					InetAddress inetAddr = inetAddrs.nextElement();
					// 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
					if (!inetAddr.isLoopbackAddress()) {
						if (inetAddr.isSiteLocalAddress()) {
							// 如果是site-local地址，就是它了 就是我们要找的
							// ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
							return inetAddr;
						}

						// 若不是site-local地址 那就记录下该地址当作候选
						if (candidateAddress == null) {
							candidateAddress = inetAddr;
						}

					}
				}
			}

			// 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
			return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws UnknownHostException {
		Set<String> ipSet = IpUtils.getLocalIps();
		boolean b = ipSet.contains("192.168.1.107");
		for(String s : ipSet){
			System.out.println(s);
		}
	}
}