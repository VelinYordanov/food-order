package com.github.velinyordanov.foodorder.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class OrderFoodId implements Serializable {
    private static final long serialVersionUID = -4069591655623399349L;

    @Column(name = "OrderId")
    private String orderId;

    @Column(name = "FoodId")
    private String foodId;

    public OrderFoodId(String orderId, String foodId) {
	this.orderId = orderId;
	this.foodId = foodId;
    }

    public OrderFoodId() {

    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }

    public String getFoodId() {
	return foodId;
    }

    public void setFoodId(String foodId) {
	this.foodId = foodId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((foodId == null) ? 0 : foodId.hashCode());
	result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
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
	OrderFoodId other = (OrderFoodId) obj;
	if (foodId == null) {
	    if (other.foodId != null)
		return false;
	} else if (!foodId.equals(other.foodId))
	    return false;
	if (orderId == null) {
	    if (other.orderId != null)
		return false;
	} else if (!orderId.equals(other.orderId))
	    return false;
	return true;
    }
}
