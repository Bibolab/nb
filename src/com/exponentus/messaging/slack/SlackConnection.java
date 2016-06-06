package com.exponentus.messaging.slack;

import java.io.IOException;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

public class SlackConnection {
	public SlackSession getConnection(String token) throws IOException {
		token = "xoxp-23444271154-23444271170-48386960787-4072e4524d";
		SlackSession session = SlackSessionFactory.createWebSocketSlackSession(token);
		session.connect();
		return session;
	}
}
