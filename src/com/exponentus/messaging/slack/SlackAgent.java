package com.exponentus.messaging.slack;

import java.io.IOException;

import com.exponentus.server.Server;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.replies.SlackChannelReply;

public class SlackAgent {
	private SlackSession connection;

	public SlackAgent() {
		SlackConnection conn = new SlackConnection();
		try {
			connection = conn.getConnection();
		} catch (IOException e) {
			Server.logger.errorLogEntry(e);
		}
	}

	public void sendMessageToAChannel(String msg) {
		SlackChannel channel = connection.findChannelByName("poema-bot");
		connection.sendMessage(channel, msg);
	}

	public void sendDirectMessageToAUser(String userName, String msg) {
		SlackUser user = connection.findUserByUserName(userName);
		connection.sendMessageToUser(user, msg, null);
	}

	public void sendDirectMessageToAUserTheHardWay(SlackSession session) {
		SlackUser user = session.findUserByUserName("kayra");
		SlackMessageHandle<SlackChannelReply> reply = session.openDirectMessageChannel(user);
		SlackChannel channel = reply.getReply().getSlackChannel();
		session.sendMessage(channel, "Hi, how are you", null);
	}

	public void sendDirectMessageToMultipleUsers(SlackSession session) {
		SlackUser kayra = session.findUserByUserName("kayra");
		SlackUser nzimas = session.findUserByUserName("nzimas");

		SlackMessageHandle<SlackChannelReply> reply = session.openMultipartyDirectMessageChannel(kayra, nzimas);
		SlackChannel channel = reply.getReply().getSlackChannel();

		session.sendMessage(channel, "Hi, how are you guys", null);
	}

}