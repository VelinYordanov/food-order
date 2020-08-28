package com.github.velinyordanov.foodorder.dto;

public class EnumDto {
    private int id;
    private String value;

    public EnumDto(int id, String value) {
	this.setId(id);
	this.setValue(value);
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }
}
