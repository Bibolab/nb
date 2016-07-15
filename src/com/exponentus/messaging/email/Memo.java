package com.exponentus.messaging.email;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.stringtemplate.v4.ST;

import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.server.Server;

public class Memo {
	private Multipart body = new MimeMultipart();
	private String subject;
	private ST rawBody;

	public Memo(String subj, String message) {
		subject = subj;
		rawBody = new ST(message, '$', '$');
	}

	public Memo(String subj, String message, IAppEntity entity) {
		subject = subj;
		try {
			rawBody = new ST(message, '$', '$');
			Class<? extends IAppEntity> clazz = entity.getClass();
			Field[] allFields = clazz.getDeclaredFields();

			for (Field each : allFields) {
				Field field;
				try {
					field = clazz.getDeclaredField(each.getName());
					field.setAccessible(true);
					Object value = field.get(entity);
					rawBody.add(field.getName(), value);
					System.out.println(field.getName() + "=" + value);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

			}

			BodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(rawBody.render(), "text/html; charset=utf-8");
			body.addBodyPart(htmlPart);
		} catch (MessagingException e) {
			Server.logger.errorLogEntry(e);
		}
	}

	public void addVar(String varName, String value) {
		rawBody.add(varName, value);
	}

	public Multipart getBody() {
		try {
			BodyPart htmlPart = new MimeBodyPart();
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
