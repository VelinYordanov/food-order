package com.github.velinyordanov.foodorder.data.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "Orders_Foods")
public class OrderFood {
    @EmbeddedId
    private OrderFoodId orderFoodId;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "OrderId")
    private Order order;

    @ManyToOne
    @MapsId("foodId")
    @JoinColumn(name = "FoodId")
    private Food food;

    @Column(name = "Quantity", nullable = false)
    private int quantity;

    public OrderFoodId getOrderFoodId() {
	return orderFoodId;
    }

    public void setOrderFoodId(OrderFoodId orderFoodId) {
	this.orderFoodId = orderFoodId;
    }

    public Order getOrder() {
	return order;
    }

    public void setOrder(Order order) {
	this.order = order;
    }

    public Food getFood() {
	return food;
    }

    public void setFood(Food food) {
	this.food = food;
    }

    public int getQuantity() {
	return quantity;
    }

    public void setQuantity(int quantity) {
	this.quantity = quantity;
    }
}
