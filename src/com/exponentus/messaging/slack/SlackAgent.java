package com.exponentus.messaging.slack;

import java.io.IOException;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.localization.Vocabulary;
import com.exponentus.log.JavaConsoleLogger;
import com.exponentus.server.Server;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.replies.SlackChannelReply;

public class SlackAgent {

	public void sendMessageToAChannel(SlackSession session) {
		SlackChannel channel = session.findChannelByName("poema-bot");
		session.sendMessage(channel, "Hey there");
	}

	public void sendDirectMessageToAUser(SlackSession session) {
		SlackUser user = session.findUserByUserName("kayra");
		session.sendMessageToUser(user, "Hi, how are you", null);
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

	public static void main(String[] args) {
		Server.logger = new JavaConsoleLogger();
		EnvConst.DATABASE_NAME = "poema";
		Environment.vocabulary = new Vocabulary("test");
		Environment.init();
		System.out.println(Environment.slackToken);
		SlackConnection conn = new SlackConnection();
		try {
			SlackSession connection = conn.getConnection();
			new SlackAgent().sendMessageToAChannel(connection);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}