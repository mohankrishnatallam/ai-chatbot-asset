package com.cx.asset.repository;

import com.cx.asset.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Order> findByOrderStatus(String orderStatus);
}