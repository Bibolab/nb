package com.exponentus.messaging.slack;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.exponentus.env.Environment;
import com.exponentus.log.JavaConsoleLogger;
import com.exponentus.messaging.MessageAgent;
import com.exponentus.server.Server;

public class SlackAgent extends MessageAgent {
	private static final String SLACK_HTTPS_AUTH_URL = "https://slack.com/api/rtm.start?token=";
	private static String authToken = Environment.slackToken;

	void get() {
		Server.logger = new JavaConsoleLogger();
		Server.logger.infoLogEntry("connecting to slack");

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SLACK_HTTPS_AUTH_URL + authToken);

		Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		Object rr = resp.readEntity(ExtSlackResponse.class);
		System.out.println(rr + "=" + rr.getClass().getCanonicalName());
	}

	void test() {
		Server.logger = new JavaConsoleLogger();
		Server.logger.infoLogEntry("connecting to slack");

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("https://slack.com/api/api.test?pretty=1");

		Response bean = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		Object rr = bean.getEntity();
		System.out.println(bean + "=" + rr.getClass().getCanonicalName());
	}

	public boolean sendMеssage(String userName, String text) {
		SlackMessage msg = new SlackMessage();
		msg.setToken(authToken);
		msg.setSender(Environment.smtpUserName);
		msg.setChannel(userName);
		msg.setText(text);

		Client client = ClientBuilder.newClient();

		WebTarget target = client.target("https://slack.com/api/chat.postMessage").queryParam("token", msg.getToken())
		        .queryParam("username", msg.getSender()).queryParam("channel", msg.getChannel()).queryParam("mrkdwn", true)
		        .queryParam("text", msg.getText());

		RunnableFuture<Boolean> f = new FutureTask<>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return send(userName, target);

			}
		});
		new Thread(f).start();
		return true;
	}

	public boolean sendMеssageSync(String userName, String text) {
		SlackMessage msg = new SlackMessage();
		msg.setToken(authToken);
		msg.setSender(Environment.smtpUserName);
		msg.setChannel(userName);
		msg.setText(text);

		Client client = ClientBuilder.newClient();

		WebTarget target = client.target("https://slack.com/api/chat.postMessage").queryParam("token", msg.getToken())
		        .queryParam("username", msg.getSender()).queryParam("channel", msg.getChannel()).queryParam("mrkdwn", true)
		        .queryParam("text", msg.getText());

		return send(userName, target);
	}

	private boolean send(String userName, WebTarget target) {
		Response bean = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		try {
			Object obj = bean.getEntity();
			System.out.println("class=" + obj.getClass().getName());
			// ClientResponse resp = (ClientResponse) obj;
			if (obj != null) {
				logger.infoLogEntry("Message has been sent to " + userName);
				return true;
			} else {
				return false;
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
			return true;
		}
	}
}
