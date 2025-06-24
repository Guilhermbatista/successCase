package com.version.demov2.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.version.demov2.Entities.OrderItem;
import com.version.demov2.Entities.OrderItemPK;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

}
