package com.cx.asset.service;

final class AiErrorMapper {

    private AiErrorMapper() {}

    static String recoverableUserMessage(Throwable e) {
        String msg = collectMessages(e);
        if (msg.contains("tool call validation failed")
                || msg.contains("tool_use_failed")) {
            if (msg.contains("shippingAddress")) {
                return "Please provide a shipping address to create your order.";
            }
            if (msg.contains("quantity")) {
                return "Please provide quantity for each product in your order.";
            }
        }
        if (msg.contains("shippingAddress") && msg.contains("expected string")) {
            return "Please provide a shipping address to create your order.";
        }
        return null;
    }

    private static String collectMessages(Throwable e) {
        StringBuilder builder = new StringBuilder();
        while (e != null) {
            if (e.getMessage() != null) {
                builder.append(e.getMessage());
            }
            e = e.getCause();
        }
        return builder.toString();
    }
}
