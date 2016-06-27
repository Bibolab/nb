package com.exponentus.messaging.slack;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.exponentus.env.Environment;
import com.exponentus.log.JavaConsoleLogger;
import com.exponentus.server.Server;

public class Connect {
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

	public static void main(String[] args) {
		Connect conn = new Connect();
		conn.get();
	}
}
