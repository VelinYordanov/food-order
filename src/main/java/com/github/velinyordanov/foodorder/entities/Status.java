package com.github.velinyordanov.foodorder.entities;

public enum Status {
    Pending(0), Delivered(1);

    private int member;

    private Status(int member) {
	this.member = member;
    }
}