package com.github.velinyordanov.foodorder.enums;

public enum UserType {
	Customer(0), Restaurant(1);

	private final int levelCode;

	private UserType(int levelCode) {
		this.levelCode = levelCode;
	}
}
