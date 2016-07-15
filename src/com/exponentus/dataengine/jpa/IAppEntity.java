package com.exponentus.dataengine.jpa;

import java.util.Date;
import java.util.UUID;

import com.exponentus.user.IUser;

public interface IAppEntity extends ISimpleAppEntity<UUID> {

	long getAuthorId();

	IUser<Long> getAuthor();

	void setAuthorId(long author);

	Date getRegDate();

	public String getForm();

	public void setForm(String form);

	public String getDefaultFormName();

	public String getDefaultViewName();

	public boolean isEditable();

	public boolean isWasRead();

	public void setEditable(boolean isEditable);

}
