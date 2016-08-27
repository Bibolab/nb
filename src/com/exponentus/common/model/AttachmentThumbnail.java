package com.exponentus.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "attachments_thumbnail")
public class AttachmentThumbnail {

    @Id
    @OneToOne
    private Attachment attachment;

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;

    public AttachmentThumbnail() {
    }

    public AttachmentThumbnail(Attachment attachment, byte[] file) {
        this.attachment = attachment;
        this.file = file;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
