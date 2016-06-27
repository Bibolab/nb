package com.exponentus.messaging.slack;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ExtSlackResponse extends SlackResponse {
	public LinkedHashMap self;
	public LinkedHashMap team;
	public ArrayList<LinkedHashMap> channels;
	public ArrayList<LinkedHashMap> groups;
	public String latest_event_ts;

	public ArrayList<LinkedHashMap> ims;
	public int cache_ts;
	public LinkedHashMap subteams;

	public LinkedHashMap dnd;
	public ArrayList<LinkedHashMap> users;

	public String cache_version;
	public String cache_ts_version;
	public ArrayList<LinkedHashMap> bots;
	public String url;

}
