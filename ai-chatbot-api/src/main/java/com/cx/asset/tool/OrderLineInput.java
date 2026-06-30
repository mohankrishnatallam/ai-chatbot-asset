package com.cx.asset.tool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tool input for {@link OrderTools#createOrder} — only productId and quantity are
 * supplied by the model; pricing fields are resolved when the order is saved.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineInput {

    private String productId;
    private Integer quantity;
}
