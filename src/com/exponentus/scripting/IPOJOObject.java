package com.exponentus.scripting;

import java.util.List;

import com.exponentus.common.model.Attachment;
import com.exponentus.scriptprocessor.page.IOutcomeObject;

public interface IPOJOObject extends IOutcomeObject {

	String getEntityKind();

	String getIdentifier();

	String getURL();

	List<Attachment> getAttachments();

	void setAttachments(List<Attachment> attachments);

	String getFullXMLChunk(_Session ses);

	String getShortXMLChunk(_Session ses);

	boolean isWasRead();

	Object getJSONObj(_Session ses);

	public boolean isEditable();

}
