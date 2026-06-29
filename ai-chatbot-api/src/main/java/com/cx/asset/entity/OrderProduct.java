package com.cx.asset.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Line item on a persisted order. {@code productId} matches {@link InventoryItem#getProductId()}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {

    private String productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
}
