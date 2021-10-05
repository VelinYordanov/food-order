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
}
