package com.exponentus.messaging.slack;

import java.io.IOException;

import com.exponentus.env.Environment;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

public class SlackConnection {
	public SlackSession getConnection() throws IOException {
		if (Environment.slackEnable) {
			SlackSession session = SlackSessionFactory.createWebSocketSlackSession(Environment.slackToken);
			session.connect();
			return session;
		} else {
			return null;
		}
	}
}
