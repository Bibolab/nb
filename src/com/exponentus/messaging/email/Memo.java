package com.exponentus.messaging.email;

import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.stringtemplate.v4.ST;

import com.exponentus.server.Server;

public class Memo {
	private Multipart body = new MimeMultipart();
	private String subject;

	public Memo(String subj, String message, Map<String, String> v) {
		subject = subj;
		try {
			ST rawBody = new ST(message, '$', '$');
			for (Map.Entry<String, String> entry : v.entrySet()) {
				rawBody.add(entry.getKey(), entry.getValue());
			}
			BodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(rawBody.render(), "text/html; charset=utf-8");
			body.addBodyPart(htmlPart);
		} catch (MessagingException e) {
			Server.logger.errorLogEntry(e);
		}
	}

	public Multipart getBody() {
		return body;
	}

	public void setBody(Multipart body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
