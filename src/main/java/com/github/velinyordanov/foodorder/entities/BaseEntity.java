package com.github.velinyordanov.foodorder.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    private String id;

    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedOn;

    public BaseEntity() {
	this.setId(UUID.randomUUID().toString());
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
}
