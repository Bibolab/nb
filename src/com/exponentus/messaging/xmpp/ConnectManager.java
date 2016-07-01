package com.exponentus.messaging.xmpp;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.exponentus.env.Environment;
import com.exponentus.server.Server;

public class ConnectManager extends Thread {
	public ChatManager chatmanager;
	public XMPPConnection connection;

	private ConnectionConfiguration config;

	public ConnectManager() {
		config = new ConnectionConfiguration(Environment.XMPPServer, Environment.XMPPServerPort);
		config.setReconnectionAllowed(true);
		SASLAuthentication.supportSASLMechanism("PLAIN");
		config.setCompressionEnabled(true);
		config.setSASLAuthenticationEnabled(true);
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (connection == null || (!connection.isConnected())) {
					connection = new XMPPConnection(config);
					// System.setProperty("smack.debugEnabled", "true");
					// connection.DEBUG_ENABLED = true;
					SASLAuthentication.supportSASLMechanism("PLAIN", 0);
					connection.connect();
					connection.login(Environment.XMPPLogin, Environment.XMPPPwd, Environment.XMPPLogin);
					InstMessageListener listener = new InstMessageListener();
					chatmanager = connection.getChatManager();
					chatmanager.addChatListener(listener);
					Server.logger.infoLogEntry("Connected...");
					Environment.XMPPServerEnable = true;
					//// Environment.connection = connection;
					// Environment.chatmanager = chatmanager;
				}
			} catch (XMPPException xmppe) {
				if (xmppe.getMessage().contains("not-authorized")) {
					Server.logger.warningLogEntry("Error while authorization to XMPP server, InstantMessengerAgent have to shutdown");
				} else {
					Server.logger.errorLogEntry("XMPPE Error while connect to XMPP server, InstantMessengerAgent have to shutdown");
					Server.logger.errorLogEntry(xmppe);
				}
			} catch (Exception e) {
				Server.logger.infoLogEntry("Error while connect to XMPP server, InstantMessengerAgent have to shutdown");
				Server.logger.errorLogEntry(e);
				Environment.XMPPServerEnable = false;
				// Environment.connection = null;
				// Environment.chatmanager = null;
			}
			try {
				Thread.sleep(5 * 1);
			} catch (InterruptedException e) {
				Server.logger.errorLogEntry(e);
			}
		}
	}
}
