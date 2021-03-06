package com.exponentus.common.model;

import java.util.UUID;

import javax.persistence.*;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.IAppFile;
import com.exponentus.scripting._Session;
import com.exponentus.user.AnonymousUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import projects.model.Project;

@Entity
@Table(name = "attachments")

// doesn't work ?
@NamedEntityGraphs({
		@NamedEntityGraph(name = Attachment.FULL_GRAPH,
				includeAllAttributes = true,
				attributeNodes = {
						@NamedAttributeNode("id"),
						@NamedAttributeNode("fieldName"),
						@NamedAttributeNode("realFileName"),
						@NamedAttributeNode("extension"),
						@NamedAttributeNode("hasThumbnail"),
						@NamedAttributeNode("comment")
				}
		),
		@NamedEntityGraph(name = Attachment.SHORT_GRAPH,
				attributeNodes = {
						@NamedAttributeNode("realFileName"),
						@NamedAttributeNode("size")
				}
		)
})

public class Attachment extends AppEntity<UUID> implements IAppFile {

	public final static String FULL_GRAPH = "Attachment.FULL_GRAPH";
	public final static String SHORT_GRAPH = "Attachment.SHORT_GRAPH";

	protected String form = "attachment";
	protected Long author = AnonymousUser.ID;

	@Column(length = 64)
	private String fieldName = "attachment";
	private String realFileName;
	@Column(length = 32)
	private String extension;
	private long size;
	private boolean hasThumbnail = false;
	private String comment;

	@JsonIgnore
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	@CascadeOnDelete
	@PrimaryKeyJoinColumn
	private AttachmentThumbnail attachmentThumbnail;

	@Transient
	private String sign = "";

	@JsonIgnore
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] file;

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getRealFileName() {
		return realFileName;
	}

	@Override
	public void setRealFileName(String realFileName) {
		// file extension
		int lastDotIndex = realFileName.lastIndexOf(".");
		if (lastDotIndex != -1) {
			extension = realFileName.substring(lastDotIndex + 1).toLowerCase();
		} else {
			extension = "";
		}
		//
		this.realFileName = realFileName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public byte[] getFile() {
		return file;
	}

	@Override
	public void setFile(byte[] file) {
		this.file = file;
		size = file.length;
	}

	@Override
	public String getSign() {
		return sign;
	}

	@Override
	public void setSign(String sign) {
		this.sign = sign;
	}

	public boolean isHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public AttachmentThumbnail getAttachmentThumbnail() {
		return attachmentThumbnail;
	}

	public void setAttachmentThumbnail(AttachmentThumbnail attachmentThumbnail) {
		this.attachmentThumbnail = attachmentThumbnail;
	}

	@Override
	public String getDefaultFormName() {
		return "attachment";
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(400);
		chunk.append("<fieldname>" + fieldName + "</fieldname>");
		chunk.append("<filename>" + realFileName + "</filename>");
		return chunk.toString();
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		return getShortXMLChunk(ses);
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
			Attachment tmp = (Attachment) obj;

			if (tmp.id == null) {
				return false;
			}

			if (tmp.fieldName != null && this.fieldName == null) {
				return false;
			}

			if (tmp.realFileName != null && this.realFileName == null) {
				return false;
			}

			if ((tmp.fieldName == null && this.fieldName == null || tmp.fieldName.equals(this.fieldName))
			        && (tmp.realFileName == null && this.realFileName == null || tmp.realFileName.equals(this.realFileName))) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return "fieldName=" + fieldName + ", realFileName=" + realFileName;
	}
}
