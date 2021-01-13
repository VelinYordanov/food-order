package com.github.velinyordanov.foodorder.data.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.security.core.userdetails.UserDetails;

@MappedSuperclass
public abstract class BaseUser extends BaseEntity implements UserDetails {
    private static final long serialVersionUID = -3258223604220059426L;

    @Column(name = "Email", unique = true, nullable = false, columnDefinition = "nvarchar(50)")
    private String email;

    @Column(name = "Name", unique = true, nullable = false, columnDefinition = "nvarchar(50)")
    private String name;

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
	return this.getEmail();
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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
