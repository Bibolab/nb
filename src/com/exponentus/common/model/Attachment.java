package com.exponentus.common.model;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.scripting._Session;
import com.exponentus.user.AnonymousUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "attachments")
public class Attachment extends AppEntity<UUID> {

    private String fieldName;
    private String realFileName;
    protected String form = "attachment";
    protected Long author = AnonymousUser.ID;
    @Transient
    private String sign;

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
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
    public String toString() {
        return fieldName + "/" + realFileName;
    }
}
