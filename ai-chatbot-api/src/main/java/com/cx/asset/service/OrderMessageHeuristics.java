package com.cx.asset.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OrderMessageHeuristics {

    private static final Pattern GREETING = Pattern.compile(
            "(?i)^(hi|hey|hello|hiya|howdy|good\\s+(morning|afternoon|evening)|greetings|yo|sup)[\\s!.?]*$");

    private static final Pattern NEW_ORDER = Pattern.compile(
            "(?i).*(create|place|make|start)\\s+(an\\s+)?order|order\\s+with|cancel\\s+order|fetch\\s+orders?|list\\s+orders?|show\\s+orders?|check\\s+inventory|inventory\\s+report|sales\\s+report|generate\\s+.*report.*");

    private static final String[] ADDRESS_PREFIXES = {
            "shipping address is ",
            "shipping address: ",
            "shipping address ",
            "delivery address is ",
            "delivery address: ",
            "address is ",
            "address: ",
            "deliver to ",
            "deliver at ",
            "ship to ",
            "ship at ",
            "my address is ",
    };

    private static final Pattern PRODUCT_ID_QTY = Pattern.compile("(?i)product\\s+(\\d+)\\s*-\\s*(\\d+)");

    private static final Pattern SHIPPING_IN_ORDER = Pattern.compile(
            "(?is).*?(?:shipping|delivery)\\s+address\\s+(?:as|is|:)?\\s*(.+)$");

    private static final Pattern ON_SHIPPING_ADDRESS = Pattern.compile(
            "(?is).*?\\bon\\s+shipping\\s+address\\s+(.+)$");

    private OrderMessageHeuristics() {
    }

    public static boolean looksLikeGreeting(String message) {
        return message != null && GREETING.matcher(message.trim()).matches();
    }

    public static boolean looksLikeNewOrderRequest(String message) {
        return message != null && NEW_ORDER.matcher(message).matches();
    }

    public static String extractShippingAddress(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }

        String trimmed = message.trim();
        if (looksLikeGreeting(trimmed) || looksLikeNewOrderRequest(trimmed)) {
            return null;
        }

        String lower = trimmed.toLowerCase();
        for (String prefix : ADDRESS_PREFIXES) {
            if (lower.startsWith(prefix)) {
                String address = trimmed.substring(prefix.length()).trim();
                return address.isBlank() ? null : address;
            }
        }

        if (lower.contains("address")) {
            int separator = lower.indexOf(" is ");
            if (separator >= 0) {
                String address = trimmed.substring(separator + 4).trim();
                return address.isBlank() ? null : address;
            }
        }

        if (trimmed.length() >= 3 && trimmed.length() <= 300) {
            return trimmed;
        }

        return null;
    }

    public static List<ParsedProductLine> parseProductLines(String message) {
        if (message == null || message.isBlank()) {
            return List.of();
        }

        List<ParsedProductLine> lines = new ArrayList<>();
        Matcher matcher = PRODUCT_ID_QTY.matcher(message);
        while (matcher.find()) {
            lines.add(new ParsedProductLine(matcher.group(1), Integer.parseInt(matcher.group(2))));
        }
        return lines;
    }

    public static String extractShippingAddressFromOrderMessage(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }

        String trimmed = message.trim();
        Matcher onShipping = ON_SHIPPING_ADDRESS.matcher(trimmed);
        if (onShipping.matches()) {
            return cleanAddress(onShipping.group(1));
        }

        Matcher shippingInOrder = SHIPPING_IN_ORDER.matcher(trimmed);
        if (shippingInOrder.matches()) {
            return cleanAddress(shippingInOrder.group(1));
        }

        return null;
    }

    private static String cleanAddress(String address) {
        if (address == null) {
            return null;
        }
        String cleaned = address.trim();
        return cleaned.isBlank() ? null : cleaned;
    }

    public record ParsedProductLine(String productId, int quantity) {}
}
