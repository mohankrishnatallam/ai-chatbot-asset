package com.cx.asset.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String orderId;

    private String userId;

    private String shippingAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String orderStatus;

    private Double orderTotalPrice;

    private String currency;

    private String customerId;

    private List<OrderProduct> products = new ArrayList<>();
}
