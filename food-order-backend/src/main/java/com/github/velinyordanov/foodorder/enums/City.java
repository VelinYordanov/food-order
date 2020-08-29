package com.github.velinyordanov.foodorder.enums;

public enum City {
    Sofia(0),
    Plovdiv(1),
    Burgas(2);

    private final int levelCode;

    private City(int levelCode) {
	this.levelCode = levelCode;
    }
}
