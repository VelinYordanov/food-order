package com.github.velinyordanov.foodorder.dto;

import java.time.LocalDate;

public class DiscountCodeListDto {
	private String id;

	private String code;

	private int discountPercentage;

	private LocalDate validFrom;

	private LocalDate validTo;

	private boolean isSingleUse;

	private boolean isOncePerUser;

	private int timesUsed;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public LocalDate getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDate validTo) {
		this.validTo = validTo;
	}

	public boolean getIsSingleUse() {
		return isSingleUse;
	}

	public void setIsSingleUse(boolean isSingleUse) {
		this.isSingleUse = isSingleUse;
	}

	public boolean getIsOncePerUser() {
		return isOncePerUser;
	}

	public void setIsOncePerUser(boolean isOncePerUser) {
		this.isOncePerUser = isOncePerUser;
	}

	public int getTimesUsed() {
		return timesUsed;
	}

	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + discountPercentage;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isOncePerUser ? 1231 : 1237);
		result = prime * result + (isSingleUse ? 1231 : 1237);
		result = prime * result + timesUsed;
		result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
		result = prime * result + ((validTo == null) ? 0 : validTo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscountCodeListDto other = (DiscountCodeListDto) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (discountPercentage != other.discountPercentage)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isOncePerUser != other.isOncePerUser)
			return false;
		if (isSingleUse != other.isSingleUse)
			return false;
		if (timesUsed != other.timesUsed)
			return false;
		if (validFrom == null) {
			if (other.validFrom != null)
				return false;
		} else if (!validFrom.equals(other.validFrom))
			return false;
		if (validTo == null) {
			if (other.validTo != null)
				return false;
		} else if (!validTo.equals(other.validTo))
			return false;
		return true;
	}
}
