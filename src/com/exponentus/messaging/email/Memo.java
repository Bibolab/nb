package com.exponentus.messaging.email;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private Map<String, String> variables = new HashMap<String, String>();

	public void addVar(String varName, String value) {
		variables.put(varName, value);
	}

	public Multipart getBody(String message) {
		try {
			BodyPart htmlPart = new MimeBodyPart();
			if (Environment.isDevMode()) {
				ScriptHelper.println(toString());
			}
			htmlPart.setContent(getST(message).render(), "text/html; charset=utf-8");
			body.addBodyPart(htmlPart);
			return body;
		} catch (MessagingException e) {
			Server.logger.errorLogEntry(e);
		}
		return body;
	}

	public String getPlainBody(String message) {
		if (Environment.isDevMode()) {
			ScriptHelper.println(toString());
		}
		return getST(message).render();
	}

	public void setBody(Multipart body) {
		this.body = body;
	}

	@Override
	public String toString() {
		String result = "-----------properties of the email form-----------\n";

		for (Entry<String, String> entry : variables.entrySet()) {
			result += entry.getKey() + " = " + entry.getValue() + "\n";
		}
		result += "-----------------------------------------------------";
		return result;

	}

	private ST getST(String m) {
		ST rawBody = new ST(m, '$', '$');
		rawBody.add("orgname", Environment.orgName);
		rawBody.add("appname", EnvConst.APP_ID);
		for (Entry<String, String> entry : variables.entrySet()) {
			rawBody.add(entry.getKey(), entry.getValue());
		}
		return rawBody;
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
