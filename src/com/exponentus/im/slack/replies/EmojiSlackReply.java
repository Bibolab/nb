package com.exponentus.im.slack.replies;

import java.util.Map;

import com.ullink.slack.simpleslackapi.replies.SlackReply;

public interface EmojiSlackReply extends SlackReply {
	String getTimestamp();

	Map<String, String> getEmojis();
}
