package com.cx.asset.tool;

import com.cx.asset.entity.InventoryItem;
import com.cx.asset.entity.Order;
import com.cx.asset.entity.OrderProduct;
import com.cx.asset.repository.InventoryItemRepository;
import com.cx.asset.repository.OrderRepository;
import com.cx.asset.service.SessionContext;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class OrderTools {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String ORDER_NOT_FOUND = "Order doesn't exist.";
    private static final String DEFAULT_CURRENCY = "USD";
    private static final double DEFAULT_UNIT_PRICE = 0.0;

    private final OrderRepository orderRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public OrderTools(OrderRepository orderRepository, InventoryItemRepository inventoryItemRepository) {
        this.orderRepository = orderRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Tool("""
            Create an order for the logged-in user with one or more products from inventory_items.
            products is a required list; each item must include productId AND quantity (minimum 1).
            Example: products=[{productId: "123456", quantity: 2}, {productId: "456789", quantity: 5}]
            Only pass productId and quantity from the user — productName, unitPrice, and totalPrice are set when saved.
            unitPrice defaults to 0 when not available. Stock is validated against inventory_items.
            If quantity is missing for any product, do NOT call this tool — ask the user for quantity first.
            shippingAddress is mandatory before the order is saved.
            If shippingAddress is missing, no order is created and the tool asks for the delivery address.
            Validates stock for all products first; if any product is unavailable, no order is saved.
            """)
    public String createOrder(List<OrderProduct> products,
                              @P(value = "shipping address for delivery", required = false) String shippingAddress) {
        String userId = requireOwnerUserId();
        if (userId == null) {
            return "User ID is required to create orders.";
        }

        String productInputError = validateProductInputs(products);
        if (productInputError != null) {
            return productInputError;
        }

        List<OrderLine> lines = normalizeProducts(products);
        if (shippingAddress == null || shippingAddress.isBlank()) {
            return "A shipping address is required to create your order. "
                    + formatProductSummary(lines)
                    + ". Please provide your delivery address. No order was created.";
        }

        StockValidation validation = validateStock(lines);
        if (!validation.unavailable().isEmpty()) {
            return formatUnavailableResponse(validation);
        }

        return saveOrder(userId, validation.resolvedLines(), shippingAddress.trim());
    }

    @Tool("Cancel order for the logged-in user")
    public Map<String, Object> cancelOrder(String orderId) {
        String userId = requireOwnerUserId();
        if (userId == null) {
            return Map.of("status", "FAILED", "message", "User ID is required to cancel orders.");
        }

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isEmpty() || !userId.equals(orderOpt.get().getUserId())) {
            return Map.of("orderId", orderId, "status", "NOT_FOUND", "message", ORDER_NOT_FOUND);
        }

        Order order = orderOpt.get();
        if ("CANCELLED".equals(order.getOrderStatus())) {
            return Map.of("orderId", orderId, "status", "CANCELLED", "message", "Order is already cancelled.");
        }

        order.setOrderStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        releaseStock(order);

        return Map.of(
                "orderId", orderId,
                "status", "CANCELLED"
        );
    }

    @Tool("Fetch all orders for the logged-in user by userId only (not session-scoped)")
    public String fetchOrders() {
        String userId = requireOwnerUserId();
        if (userId == null) {
            return "User ID is required to fetch orders.";
        }

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (orders.isEmpty()) {
            return "No orders found for your account.";
        }

        StringBuilder details = new StringBuilder("Your orders (all sessions):\n");
        for (int i = 0; i < orders.size(); i++) {
            details.append(i + 1).append(". ").append(formatOrderDetails(orders.get(i)));
            if (i < orders.size() - 1) {
                details.append("\n");
            }
        }
        return details.toString();
    }

    private String saveOrder(String userId, List<ResolvedLine> lines, String shippingAddress) {
        LocalDateTime now = LocalDateTime.now();
        String orderId = "ORD-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(100000, 999999);

        List<OrderProduct> orderProducts = new ArrayList<>();
        double orderTotal = 0.0;

        for (ResolvedLine line : lines) {
            reserveStock(line.productId(), line.quantity());

            double unitPrice = line.unitPrice();
            double lineTotal = unitPrice * line.quantity();
            orderTotal += lineTotal;

            orderProducts.add(new OrderProduct(
                    line.productId(),
                    line.productName(),
                    line.quantity(),
                    unitPrice,
                    lineTotal
            ));
        }

        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setCreatedBy(userId);
        order.setCustomerId(null);
        order.setShippingAddress(shippingAddress);
        order.setOrderStatus("CREATED");
        order.setOrderTotalPrice(orderTotal);
        order.setCurrency(DEFAULT_CURRENCY);
        order.setProducts(orderProducts);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        orderRepository.save(order);

        StringBuilder response = new StringBuilder("Order created successfully. Order ID: ")
                .append(orderId)
                .append(" | Shipping: ")
                .append(shippingAddress)
                .append(" | Total: ")
                .append(DEFAULT_CURRENCY)
                .append(" ")
                .append(String.format("%.2f", orderTotal))
                .append("\nProducts:");
        for (int i = 0; i < orderProducts.size(); i++) {
            OrderProduct product = orderProducts.get(i);
            response.append("\n  ")
                    .append(i + 1)
                    .append(". ")
                    .append(product.getProductId());
            if (product.getProductName() != null) {
                response.append(" (").append(product.getProductName()).append(")");
            }
            response.append(" x").append(product.getQuantity())
                    .append(" @ ")
                    .append(DEFAULT_CURRENCY)
                    .append(" ")
                    .append(String.format("%.2f", priceOrZero(product.getUnitPrice())))
                    .append(" = ")
                    .append(DEFAULT_CURRENCY)
                    .append(" ")
                    .append(String.format("%.2f", priceOrZero(product.getTotalPrice())));
        }
        return response.toString();
    }

    private StockValidation validateStock(List<OrderLine> lines) {
        List<ResolvedLine> resolved = new ArrayList<>();
        List<String> unavailable = new ArrayList<>();

        for (OrderLine line : lines) {
            Optional<InventoryItem> inventoryOpt = findByProductId(line.productId());
            if (inventoryOpt.isEmpty()) {
                unavailable.add(line.productId() + " x" + line.quantity() + " (not found in inventory)");
                continue;
            }

            InventoryItem inventory = inventoryOpt.get();
            int availableStock = availableStock(inventory);
            if (availableStock < line.quantity()) {
                unavailable.add(line.productId() + " x" + line.quantity()
                        + " (available: " + availableStock + ")");
                continue;
            }

            resolved.add(resolveLine(line, inventory));
        }

        return new StockValidation(resolved, unavailable);
    }

    private ResolvedLine resolveLine(OrderLine line, InventoryItem inventory) {
        return new ResolvedLine(
                inventory.getProductId(),
                null,
                line.quantity(),
                DEFAULT_UNIT_PRICE
        );
    }

    private double priceOrZero(Double price) {
        return price != null ? price : DEFAULT_UNIT_PRICE;
    }

    private String formatUnavailableResponse(StockValidation validation) {
        StringBuilder response = new StringBuilder("Failed to create order: insufficient stock or product not found.\nUnavailable:");
        for (int i = 0; i < validation.unavailable().size(); i++) {
            response.append("\n  ").append(i + 1).append(". ").append(validation.unavailable().get(i));
        }
        if (!validation.resolvedLines().isEmpty()) {
            response.append("\nAvailable:");
            for (int i = 0; i < validation.resolvedLines().size(); i++) {
                ResolvedLine line = validation.resolvedLines().get(i);
                response.append("\n  ").append(i + 1).append(". ")
                        .append(line.productId()).append(" x").append(line.quantity());
            }
        }
        return response.toString();
    }

    private String validateProductInputs(List<OrderProduct> products) {
        if (products == null || products.isEmpty()) {
            return "Failed to create order: at least one product with productId and quantity is required.";
        }

        List<String> missingQuantity = new ArrayList<>();
        List<String> missingProductId = new ArrayList<>();
        boolean hasValidLine = false;

        for (OrderProduct product : products) {
            if (product == null) {
                continue;
            }

            boolean hasProductId = product.getProductId() != null && !product.getProductId().isBlank();
            Integer quantity = product.getQuantity();
            boolean hasQuantity = quantity != null && quantity >= 1;

            if (hasProductId && hasQuantity) {
                hasValidLine = true;
                continue;
            }
            if (hasProductId) {
                missingQuantity.add(product.getProductId().trim());
            } else if (hasQuantity) {
                missingProductId.add("quantity " + quantity);
            }
        }

        if (!missingQuantity.isEmpty()) {
            return "Please provide quantity for product(s): "
                    + String.join(", ", missingQuantity)
                    + ". No order was created.";
        }
        if (!missingProductId.isEmpty()) {
            return "Please provide productId for each line item. No order was created.";
        }
        if (!hasValidLine) {
            return "Failed to create order: each product must include productId and quantity (minimum 1).";
        }
        return null;
    }

    private List<OrderLine> normalizeProducts(List<OrderProduct> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> merged = new LinkedHashMap<>();
        for (OrderProduct product : products) {
            if (product == null || product.getProductId() == null || product.getProductId().isBlank()) {
                continue;
            }
            Integer quantity = product.getQuantity();
            if (quantity == null || quantity < 1) {
                continue;
            }
            String productId = product.getProductId().trim();
            merged.merge(productId, quantity, Integer::sum);
        }

        List<OrderLine> lines = new ArrayList<>();
        merged.forEach((productId, quantity) -> lines.add(new OrderLine(productId, quantity)));
        return lines;
    }

    private String formatProductSummary(List<OrderLine> lines) {
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            OrderLine line = lines.get(i);
            if (i > 0) {
                summary.append(", ");
            }
            summary.append(line.productId()).append(" qty ").append(line.quantity());
        }
        return summary.toString();
    }

    private void releaseStock(Order order) {
        if (order.getProducts() == null) {
            return;
        }
        for (OrderProduct product : order.getProducts()) {
            if (product.getProductId() == null || product.getQuantity() == null || product.getQuantity() < 1) {
                continue;
            }
            findByProductId(product.getProductId()).ifPresent(item -> {
                item.setReserved(Math.max(0, item.getReserved() - product.getQuantity()));
                item.setAvailable(item.getStock() - item.getReserved() > 0);
                item.setUpdatedAt(LocalDateTime.now());
                inventoryItemRepository.save(item);
            });
        }
    }

    private void reserveStock(String productId, int quantity) {
        findByProductId(productId).ifPresent(item -> {
            item.setReserved(item.getReserved() + quantity);
            item.setAvailable(item.getStock() - item.getReserved() > 0);
            item.setUpdatedAt(LocalDateTime.now());
            inventoryItemRepository.save(item);
        });
    }

    private Optional<InventoryItem> findByProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            return Optional.empty();
        }
        return inventoryItemRepository.findByProductId(productId.trim());
    }

    private int availableStock(InventoryItem item) {
        return item.getStock() - item.getReserved();
    }

    private String requireOwnerUserId() {
        String userId = SessionContext.getUserId();
        if (userId == null || userId.isBlank()) {
            return null;
        }
        return userId;
    }

    private String formatOrderDetails(Order order) {
        StringBuilder details = new StringBuilder()
                .append("Order ID: ").append(order.getOrderId())
                .append(" | Status: ").append(order.getOrderStatus())
                .append(" | Total: ")
                .append(order.getCurrency() != null ? order.getCurrency() : DEFAULT_CURRENCY)
                .append(" ")
                .append(String.format("%.2f", order.getOrderTotalPrice() != null ? order.getOrderTotalPrice() : 0.0))
                .append(" | Shipping: ")
                .append(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A")
                .append(" | Created: ")
                .append(order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_FORMAT) : "N/A");

        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            details.append(" | Products: ");
            for (int i = 0; i < order.getProducts().size(); i++) {
                OrderProduct product = order.getProducts().get(i);
                if (i > 0) {
                    details.append("; ");
                }
                details.append(product.getProductId())
                        .append(" x")
                        .append(product.getQuantity());
            }
        }
        return details.toString();
    }

    private record OrderLine(String productId, int quantity) {}

    private record ResolvedLine(String productId, String productName, int quantity, double unitPrice) {}

    private record StockValidation(List<ResolvedLine> resolvedLines, List<String> unavailable) {}
}
