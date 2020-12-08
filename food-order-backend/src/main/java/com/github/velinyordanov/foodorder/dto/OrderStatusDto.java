package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotNull;

import com.github.velinyordanov.foodorder.data.entities.Status;

public class OrderStatusDto {
    @NotNull(message = "Status is required")
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
