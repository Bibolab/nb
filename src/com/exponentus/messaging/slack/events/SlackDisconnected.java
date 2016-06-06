package com.exponentus.messaging.slack.events;

import com.ullink.slack.simpleslackapi.SlackPersona;
import com.ullink.slack.simpleslackapi.events.SlackEvent;

public interface SlackDisconnected extends SlackEvent {

	SlackPersona getDisconnectedPersona();
}