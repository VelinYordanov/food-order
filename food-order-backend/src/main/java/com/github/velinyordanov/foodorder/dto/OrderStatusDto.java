package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotNull;

import com.github.velinyordanov.foodorder.data.entities.Status;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_STATUS;

public class OrderStatusDto {
	@NotNull(message = EMPTY_STATUS)
	private Status status;

	public OrderStatusDto() {
	}

	public OrderStatusDto(@NotNull(message = "Status is required") Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		OrderStatusDto other = (OrderStatusDto) obj;
		if (status != other.status)
			return false;
		return true;
	}
}
