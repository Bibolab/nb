package com.exponentus.common.model;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.IAppFile;
import com.exponentus.scripting._Session;
import com.exponentus.user.AnonymousUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "attachments")
public class Attachment extends AppEntity<UUID> implements IAppFile {

    private String fieldName = "attachment";
    private String realFileName;
    @Column(length = 32)
    private String extension;
    private long size;
    protected String form = "attachment";
    protected Long author = AnonymousUser.ID;
    private boolean hasThumbnail = false;
    @Transient
    private String sign = "";

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;

    @PrePersist
    protected void prePersist() {
        super.prePersist();
        // file extension
        int lastDotIndex = realFileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            extension = realFileName.substring(lastDotIndex + 1).toLowerCase();
        } else {
            extension = "";
        }
        // file size
        size = file.length;
    }

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
