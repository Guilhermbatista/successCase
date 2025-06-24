package com.version.demov2.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.version.demov2.Entities.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{

}
