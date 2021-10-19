package com.github.velinyordanov.foodorder.data.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.security.core.userdetails.UserDetails;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

@MappedSuperclass
public abstract class BaseUser extends BaseEntity implements UserDetails {
	private static final long serialVersionUID = -3258223604220059426L;

	@NotBlank(message = EMPTY_EMAIL)
	@Email(message = NOT_EMAIL)
	@Size(min = MIN_LENGTH_EMAIL, max = MAX_LENGTH_EMAIL, message = EMAIL_OUT_OF_BOUNDS)
	@Column(name = "Email", unique = true, nullable = false, columnDefinition = "nvarchar(100)")
	private String email;

	@NotBlank(message = EMPTY_PASSWORD)
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
	
	public abstract String getName();
	
	public abstract void setName(String name);

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
