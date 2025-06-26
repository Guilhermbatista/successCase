package com.version.demov2.Tests;

import java.time.Instant;


import com.version.demov2.Entities.Order;
import com.version.demov2.Entities.OrderItem;
import com.version.demov2.Entities.OrderStatus;
import com.version.demov2.Entities.Payment;
import com.version.demov2.Entities.Product;
import com.version.demov2.Entities.User;

public class OrderFactory {

	public static Order createOrder(User client) {
		
		Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, client, new Payment());
		
		Product product = ProductFactory.createProduct();
		OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
		order.getItems().add(orderItem);

		return order;
	}
}
