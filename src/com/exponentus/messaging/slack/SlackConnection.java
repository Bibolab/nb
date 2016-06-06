package com.exponentus.messaging.slack;

import java.io.IOException;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

public class SlackConnection {
	public SlackSession getConnection(String token) throws IOException {
		token = "";
		SlackSession session = SlackSessionFactory.createWebSocketSlackSession(token);
		session.connect();
		return session;
	}
}
