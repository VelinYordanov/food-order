package com.github.velinyordanov.foodorder.data.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class BaseEntity {
	@Id
	@Column(name = "Id")
	private String id;

	@Column(name = "IsDeleted")
	private boolean isDeleted;

	@Column(name = "CreatedOn", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@Column(name = "DeletedOn")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedOn;

	public BaseEntity() {
		this.setId(UUID.randomUUID().toString());
		OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
		this.setCreatedOn(Date.from(utc.toInstant()));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean getIsDeleted() {
		return isDeleted;
	}

	@Override
	public String toString() {
		return "BaseEntity [getId()=" + getId() + ", getIsDeleted()=" + getIsDeleted() + ", getCreatedOn()="
				+ getCreatedOn() + ", getDeletedOn()=" + getDeletedOn() + "]";
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
