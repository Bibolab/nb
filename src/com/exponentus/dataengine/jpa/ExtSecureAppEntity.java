package com.exponentus.dataengine.jpa;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import com.exponentus.common.model.ReadingMark;
import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class ExtSecureAppEntity extends SecureAppEntity<UUID> {
	@JsonIgnore
	@OneToOne(cascade = { CascadeType.ALL }, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = true)
	private List<ReadingMark> marks;
}
