package com.exponentus.messaging.email;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.stringtemplate.v4.ST;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.scriptprocessor.ScriptHelper;
import com.exponentus.server.Server;

public class Memo {
	private Multipart body = new MimeMultipart();
	private String subject;
	private ST rawBody;

	public Memo(String subj, String message) {
		subject = subj;
		rawBody = new ST(message, '$', '$');
		addVar("orgname", Environment.orgName);
		addVar("appname", EnvConst.APP_ID);

	}

	public void addVar(String varName, String value) {
		rawBody.add(varName, value);
	}

	public Multipart getBody() {
		try {
			BodyPart htmlPart = new MimeBodyPart();
			if (Environment.isDevMode()) {
				ScriptHelper.println(toString());
			}
			htmlPart.setContent(rawBody.render(), "text/html; charset=utf-8");
			body.addBodyPart(htmlPart);
			return body;
		} catch (MessagingException e) {
			Server.logger.errorLogEntry(e);
		}
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

	@Override
	public String toString() {
		String result = "-----------properties of the email form-----------\n";

		for (Entry<String, Object> entry : rawBody.getAttributes().entrySet()) {
			result += entry.getKey() + " = " + entry.getValue() + "\n";
		}
		result += "-----------------------------------------------------";
		return result;

	}

	@SuppressWarnings("unused")
	private static Iterable<Field> getFieldsUpTo(Class<?> startClass, Class<?> exclusiveParent) {

		List<Field> currentClassFields = new ArrayList<Field>(Arrays.asList(startClass.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

}
