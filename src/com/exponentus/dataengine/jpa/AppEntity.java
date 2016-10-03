package com.exponentus.dataengine.jpa;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.UuidGenerator;
import org.eclipse.persistence.internal.indirection.jdk8.IndirectList;

import com.exponentus.appenv.AppEnv;
import com.exponentus.common.model.Attachment;
import com.exponentus.dataengine.jpa.util.UUIDConverter;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._Session;
import com.exponentus.user.IUser;
import com.exponentus.util.TimeUtil;
import com.exponentus.util.XMLUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import administrator.dao.UserDAO;

@MappedSuperclass
@Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
@UuidGenerator(name = "uuid-gen")
public abstract class AppEntity<K extends UUID> implements IAppEntity, IPOJOObject {

	@Id
	@GeneratedValue(generator = "uuid-gen")
	@Convert("uuidConverter")
	@Column(name = "id", nullable = false)
	protected UUID id;

	@Column(name = "author", nullable = false, updatable = false)
	protected Long author;

	@Transient
	private String authorName;

	// @Column(name = "reg_date", nullable = false, updatable = false,
	// columnDefinition = "DATE DEFAULT CURRENT_DATE")
	@Column(name = "reg_date", nullable = false, updatable = false)
	protected Date regDate;

	@Column(name = "form", nullable = false, updatable = false, length = 64)
	protected String form;

	@JsonIgnore
	@Transient
	protected boolean isEditable = true;

	@JsonIgnore
	@Transient
	protected boolean isWasRead = true;

	@PrePersist
	protected void prePersist() {
		regDate = new Date();
	}

	@Override
	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@JsonIgnore
	@Override
	public String getIdentifier() {
		if (id != null) {
			return id.toString();
		} else {
			return "null";
		}

	}

	@Override
	public long getAuthorId() {
		return author == null ? 0 : author;
	}

	@JsonIgnore
	@Override
	public IUser<Long> getAuthor() {
		return new UserDAO().findById(author);
	}

	@Override
	public void setAuthorId(long author) {
		this.author = author;
	}

	public void setAuthor(IUser<Long> user) {
		author = user.getId();
	}

	@JsonIgnore
	public void setAuthorName(long author) {
		this.author = author;
	}

	@Override
	public Date getRegDate() {
		return regDate;
	}

	@JsonIgnore
	@Override
	public String toString() {
		UUID id = getId();
		if (id != null) {
			return id.toString() + " " + this.getClass().getName();
		} else {
			return "null " + this.getClass().getName();
		}
	}

	/**
	 * To more faster processing the method should be reloaded in real entity
	 * object
	 */
	@Override
	public String getFullXMLChunk(_Session ses) {
		Class<?> noparams[] = {};
		StringBuilder value = new StringBuilder(1000);
		try {
			for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors()) {
				Method method = propertyDescriptor.getReadMethod();
				if (method != null && !method.getName().equals("getShortXMLChunk") && !method.getName().equals("getFullXMLChunk")
				        && !method.getName().equals("getClass")) {
					String methodValue = "";

					String methodName = method.getName().toLowerCase();
					String fieldName;
					if (methodName.startsWith("get")) {
						fieldName = methodName.substring(3);
					} else {
						fieldName = methodName;
					}
					try {
						Object val = method.invoke(this, noparams);
						// System.out.println(val.getClass().getName());
						if (val instanceof Date) {
							methodValue = TimeUtil.dateToStringSilently((Date) val);
						} else if (val instanceof IndirectList) {
							@SuppressWarnings("unchecked")
							List<IPOJOObject> list = (List<IPOJOObject>) val;
							for (IPOJOObject nestedValue : list) {
								// methodValue += nestedValue.toXML();
								methodValue = nestedValue.getClass().getName();
							}
						} else if (val.getClass().isInstance(IPOJOObject.class)) {
							methodValue = ((IPOJOObject) val).getFullXMLChunk(null);
						} else {
							methodValue = val.toString();
						}
					} catch (Exception e) {
						// AppEnv.logger.errorLogEntry(e);
					}
					value.append("<" + fieldName + ">" + XMLUtil.getAsTagValue(methodValue) + "</" + fieldName + ">");
				}
			}
		} catch (IntrospectionException e) {
			AppEnv.logger.errorLogEntry(e);
		}

		return value.toString();
	}

	/**
	 * To more faster processing the method during showing in a view should be
	 * reloaded in real entity object
	 */
	@JsonIgnore
	@Override
	public String getShortXMLChunk(_Session ses) {
		return getFullXMLChunk(ses);
	}

	@JsonIgnore
	@Override
	public Object getJSONObj(_Session ses) {
		return this;
	}

	@Override
	public String getURL() {
		return "p?id=" + this.getClass().getSimpleName().toLowerCase() + "-form&amp;docid=" + getIdentifier();
	}

	@JsonIgnore
	@Override
	public String getDefaultFormName() {
		return this.getClass().getSimpleName().toLowerCase() + "-form";
	}

	@JsonIgnore
	@Override
	public String getDefaultViewName() {
		return this.getClass().getSimpleName().toLowerCase() + "-view";
	}

	@JsonIgnore
	@Override
	public String getForm() {
		return form;
	}

	@Override
	public void setForm(String form) {
		this.form = form;
	}

	@JsonProperty("isNew")
	public boolean isNew() {
		return id == null;
	}

	@Override
	public boolean isEditable() {
		return isEditable;
	}

	@Override
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	@JsonProperty("kind")
	@Override
	public String getEntityKind() {
		return this.getClass().getSimpleName().toLowerCase();
	}

	@Override
	public boolean isWasRead() {
		return isWasRead;
	}

	public void setWasRead(boolean isWasRead) {
		this.isWasRead = isWasRead;
	}

	@Override
	public List<Attachment> getAttachments() {
		return new ArrayList<>();
	}

	@Override
	public void setAttachments(List<Attachment> attachments) {

	}

	@JsonIgnore
	public String getViewText() {
		return form;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(getClass() == obj.getClass())) {
			return false;
		} else {
			@SuppressWarnings("unchecked")
			AppEntity<UUID> tmp = (AppEntity<UUID>) obj;

			if (tmp.id == null) {
				return false;
			}

			if (tmp.id.equals(this.id)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
