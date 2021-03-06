package com.exponentus.dataengine.jpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;

import com.exponentus.user.IUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class SecureAppEntity<K extends UUID> extends AppEntity<UUID> {

	@JsonIgnore
	@ElementCollection
	private Set<Long> editors = new HashSet<>();

	@JsonIgnore
	@ElementCollection
	private Set<Long> readers = new HashSet<>();

	public Set<Long> getEditors() {
		return editors;
	}

	public void setEditors(Set<Long> editors) {
		this.editors = editors;
	}

	public void addReaderEditor(IUser<Long> user) {
		long id = user.getId();
		addReaderEditor(id);
	}

	public void addReaderEditor(Long id) {
		if (id != 0) {
			editors.add(id);
			readers.add(id);
		}
	}

	public Set<Long> getReaders() {
		return readers;
	}

	public void setReaders(Set<Long> readers) {
		this.readers = readers;
	}

	public void addReader(IUser<Long> user) {
		long id = user.getId();
		if (id > 0) {
			this.readers.add(id);
		}
	}

	public void addReaders(List<Long> r) {
		readers.addAll(r);
	}

	public void addReaders(Set<Long> r) {
		readers.addAll(new ArrayList<>(r));

	}

	public void resetReadersEditors() {
		editors.clear();
		readers.clear();
	}

	public void resetEditors() {
		editors.clear();
		editors.add(author);
	}

	@Override
	public void setAuthor(IUser<Long> user) {
		author = user.getId();
		if (author > 0) {
			addReader(user);
			addReaderEditor(user);
		}
	}

	@Override
	public boolean isEditable() {
		return isEditable;
	}

}
