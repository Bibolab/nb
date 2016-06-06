package com.exponentus.messaging.slack.listeners;

import com.exponentus.messaging.slack.events.SlackDisconnected;
import com.ullink.slack.simpleslackapi.listeners.SlackEventListener;

public interface SlackDisconnectedListener extends SlackEventListener<SlackDisconnected> {
}