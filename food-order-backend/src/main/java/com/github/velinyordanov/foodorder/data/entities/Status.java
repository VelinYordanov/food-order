package com.github.velinyordanov.foodorder.data.entities;

public enum Status {
	Pending(0), Delivered(1), Accepted(2), Cancelled(3);

	private int member;

	private Status(int member) {
		this.member = member;
	}
}