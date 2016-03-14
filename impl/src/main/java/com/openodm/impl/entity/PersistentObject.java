package com.openodm.impl.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class PersistentObject implements Serializable {

	private static final long serialVersionUID = 2164829167665250690L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_ADDED", nullable = false, updatable = false)
	private Calendar dateAdded = Calendar.getInstance(TimeZone
			.getTimeZone("UTC"));

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_LAST_MODIFIED", nullable = false)
	private Calendar dateLastModified = Calendar.getInstance(TimeZone
			.getTimeZone("UTC"));

	@Column(name = "CREATOR", nullable = false, length = 255, updatable = false)
	private String creator;

	@Column(name = "UPDATED_BY", nullable = false, length = 255)
	private String updatedBy;

	@Column(name = "STATUS", nullable = false, length = 32)
	@Enumerated(EnumType.STRING)
	private ObjectStatus status = ObjectStatus.active;

	public Calendar getDateAdded() {
		return dateAdded;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Calendar getDateLastModified() {
		return dateLastModified;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public ObjectStatus getStatus() {
		return status;
	}

	public void setStatus(ObjectStatus status) {
		this.status = status;
	}

	public void setDateLastModified(Calendar dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

}