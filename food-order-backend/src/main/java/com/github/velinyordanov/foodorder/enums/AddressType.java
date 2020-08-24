package com.github.velinyordanov.foodorder.enums;

public enum AddressType {
    Unspecified(0),
    ApartmentBuilding(1),
    House(2),
    OfficeBuilding(3),
    Other(4);

    private final int levelCode;

    private AddressType(int levelCode) {
	this.levelCode = levelCode;
    }
}
