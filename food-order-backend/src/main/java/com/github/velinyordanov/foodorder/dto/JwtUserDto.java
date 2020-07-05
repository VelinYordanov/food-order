package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;

public class JwtUserDto {
    private String id;
    private String username;
    private Collection<String> authorities;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public Collection<String> getAuthorities() {
	return authorities;
    }

    public void setAuthorities(Collection<String> authorities) {
	this.authorities = authorities;
    }

    @Override
    public String toString() {
	return "JwtUserDto [getId()=" + getId()
		+ ", getUsername()="
		+ getUsername()
		+ ", getAuthorities()="
		+ getAuthorities()
		+ "]";
    }
}
