package com.github.velinyordanov.foodorder.data.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.security.core.userdetails.UserDetails;

@MappedSuperclass
public abstract class BaseUser extends BaseEntity implements UserDetails {
    private static final long serialVersionUID = -3258223604220059426L;

    @Column(name = "Username", unique = true, nullable = false)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "IsAccountNonExpired")
    private boolean isAccountNonExpired;

    @Column(name = "IsAccountNonLocked")
    private boolean isAccountNonLocked;

    @Column(name = "IsCredentialsNonExpired")
    private boolean isCredentialsNonExpired;

    @Column(name = "IsEnabled")
    private boolean isEnabled;

    public BaseUser() {
	super();

	this.setAccountNonExpired(true);
	this.setAccountNonLocked(true);
	this.setCredentialsNonExpired(true);
	this.setEnabled(true);
    }

    @Override
    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    @Override
    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
	return isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean isAccountNonExpired) {
	this.isAccountNonExpired = isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
	return isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean isAccountNonLocked) {
	this.isAccountNonLocked = isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
	return isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean isCredentialsNonExpired) {
	this.isCredentialsNonExpired = isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
	return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
	this.isEnabled = isEnabled;
    }
}
