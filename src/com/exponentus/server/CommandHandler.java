package com.exponentus.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CommandHandler implements ActionListener {// , MessageListener{
	@Override
	public void actionPerformed(ActionEvent ae) {

	}

	/*
	 * public void processMessage(Chat chat, Message message) {
	 * processCommand(message.getBody()); }
	 */
	public void processCommand(String command) {
		try {

			if (!command.equals("")) {

			}
		} catch (Exception e) {
			Server.logger.errorLogEntry(e);
		}
	}

}