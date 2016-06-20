package com.exponentus.scripting;

import java.util.ArrayList;
import java.util.List;

import com.exponentus.common.model.Attachment;

public class POJOObjectAdapter<UUID> implements IPOJOObject {

	@Override
	public String getURL() {
		return "Provider";
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		return "<object>null</object>";
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		return getFullXMLChunk(ses);
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public Object getJSONObj(_Session ses) {
		return this;
	}

	@Override
	public String getIdentifier() {
		return "null";
	}

	@Override
	public boolean isWasRead() {
		return true;
	}

	@Override
	public String getEntityKind() {
		return this.getClass().getSimpleName().toLowerCase();
	}

	@Override
	public List<Attachment> getAttachments() {
		return new ArrayList<Attachment>();
	}

	@Override
	public void setAttachments(List<Attachment> attachments) {

	}

}
