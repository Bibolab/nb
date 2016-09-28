package com.exponentus.util;

import java.io.IOException;
import java.net.Socket;

public class NetUtil {
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
