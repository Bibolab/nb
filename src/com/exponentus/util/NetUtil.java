package com.exponentus.util;

import java.io.IOException;
import java.net.Socket;

import net.firefang.ip2c.Country;
import net.firefang.ip2c.IP2Country;

public class NetUtil {
	private static final int caching = IP2Country.MEMORY_CACHE;

	public static boolean portAvailable(String host, int port) {
		Socket s = null;
		try {
			s = new Socket(host, port);
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					// throw new RuntimeException("You should handle this
					// error.", e);
				}
			}
		}
	}

	public static String getCountry(String ip) {
		try {
			IP2Country ip2c = new IP2Country(caching);
			Country c = ip2c.getCountry(ip);
			return c.getName();
		} catch (IOException e) {

		}
		return "unknown";

	}

	public static void main(String[] arg) throws InterruptedException {
		int pings = 100;
		for (int i = 0; pings > i; i++) {
			if (portAvailable("localhost", 8770)) {
				System.out.println("port free");
			} else {
				System.out.println("port busy");
			}
			Thread.sleep(1000);
		}
	}
}
