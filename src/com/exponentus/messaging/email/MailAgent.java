package com.exponentus.messaging.email;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.exponentus.env.Environment;
import com.exponentus.exception.MsgException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.messaging.MessageAgent;
import com.sun.mail.util.MailConnectException;

public class MailAgent extends MessageAgent {
	private Session mailerSes;

	public MailAgent() {
		if (Environment.mailEnable) {
			Properties props = new Properties();
			props.put("mail.smtp.host", Environment.SMTPHost);
			if (Environment.smtpAuth) {
				props.put("mail.smtp.auth", Environment.smtpAuth);
				props.put("mail.smtp.port", Environment.smtpPort);
				if ("465".equals(Environment.smtpPort)) {
					props.put("mail.smtp.ssl.enable", "true");
				}
				Authenticator auth = new SMTPAuthenticator();
				mailerSes = Session.getInstance(props, auth);
			} else {
				mailerSes = Session.getInstance(props, null);
			}

		}

	}

	public boolean sendMеssage(List<String> recipients, Memo mailMessage) throws MsgException {
		MimeMessage msg = new MimeMessage(mailerSes);

		try {
			msg.setContent(mailMessage.getBody());
			msg.setSubject(mailMessage.getSubject(), "utf-8");
		} catch (MessagingException e) {
			String error = e.toString();
			logger.errorLogEntry(e);
			throw new MsgException(error, LanguageCode.ENG);
		}

		RunnableFuture<Boolean> f = new FutureTask<>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return send(msg, recipients);
			}
		});
		new Thread(f).start();
		// return f.get();
		return true;
	}

	public boolean sendMеssageSync(Memo mailMessage, List<String> recipients) throws MsgException {
		MimeMessage msg = new MimeMessage(mailerSes);

		try {
			msg.setContent(mailMessage.getBody());
			msg.setSubject(mailMessage.getSubject(), "utf-8");
		} catch (MessagingException e) {
			String error = e.toString();
			logger.errorLogEntry(e);
			throw new MsgException(error, LanguageCode.ENG);
		}

		return send(msg, recipients);

	}

	private boolean send(MimeMessage msg, List<String> recipients) throws MsgException {
		try {
			msg.setFrom(new InternetAddress(Environment.smtpUser, Environment.smtpUserName));
			for (String recipient : recipients) {
				try {
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				} catch (AddressException ae) {
					logger.warningLogEntry("incorrect e-mail \"" + recipient + "\"");
					continue;
				}
			}
		} catch (MessagingException e) {
			logger.errorLogEntry(e);
		} catch (UnsupportedEncodingException e) {
			logger.errorLogEntry(e);
		}

		try {
			if (Environment.mailEnable) {
				Transport.send(msg);
				logger.infoLogEntry("Message has been sent to " + recipients);
				return true;
			} else {
				logger.warningLogEntry("Mail agent disabled");
			}
		} catch (MailConnectException e) {
			logger.errorLogEntry(e);
		} catch (SendFailedException se) {
			if (se.getMessage().contains("relay rejected for policy reasons")) {
				String error = "relay rejected for policy reasons by SMTP server. Message has not sent";
				logger.warningLogEntry(error);
				throw new MsgException(error, LanguageCode.ENG);
			} else if (se.getMessage().contains("No recipient addresses")) {
				String error = "No recipient addresses. Message has not sent. Recipients=" + recipients;
				logger.warningLogEntry(error);
				throw new MsgException(error, LanguageCode.ENG);
			} else {
				String error = "unable to send a message, probably SMTP host did not set";
				logger.errorLogEntry(error);
				logger.errorLogEntry(se);
				throw new MsgException(error, LanguageCode.ENG);
			}
		} catch (AuthenticationFailedException e) {
			String error = "SMTP authentication exception, smtp user=" + Environment.smtpUser;
			logger.errorLogEntry(e);
			throw new MsgException(error, LanguageCode.ENG);
		} catch (MessagingException e) {
			String error = e.toString();
			logger.errorLogEntry(e);
			throw new MsgException(error, LanguageCode.ENG);
		}
		return false;

	}

	class SMTPAuthenticator extends javax.mail.Authenticator {

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(Environment.smtpUser, Environment.smtpPassword);
		}
	}

}
