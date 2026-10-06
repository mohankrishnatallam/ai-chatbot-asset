package com.cx.asset.tool;

import com.cx.asset.dto.AiResponse;
import com.cx.asset.entity.Order;
import com.cx.asset.repository.OrderRepository;
import com.cx.asset.service.SessionContext;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Component
public class ReportingTools {

    private static final DateTimeFormatter MONTH_LABEL =
            DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    private final OrderRepository orderRepository;

    public ReportingTools(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<AiResponse> tryBuildChartResponse(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }

        String text = message.toLowerCase(Locale.ENGLISH);
        if (isSalesTrendRequest(text)) {
            return Optional.of(monthlySalesChart());
        }
        if (isOrderStatusRequest(text)) {
            return Optional.of(orderStatusChart());
        }
        if (isMonthlyOrdersRequest(text)) {
            return Optional.of(monthlyOrdersChart());
        }
        return Optional.empty();
    }

    @Tool("Build a monthly sales trend report for the logged-in user's orders")
    public Map<String, Object> generateSalesReport() {
        return chartData(monthlySalesChart());
    }

    @Tool("Build a monthly order count report for the logged-in user's orders")
    public Map<String, Object> generateMonthlyOrdersReport() {
        return chartData(monthlyOrdersChart());
    }

    private AiResponse monthlyOrdersChart() {
        Map<YearMonth, Long> monthlyOrders = new TreeMap<>();
        for (Order order : userOrders()) {
            if (order.getCreatedAt() != null) {
                monthlyOrders.merge(YearMonth.from(order.getCreatedAt()), 1L, Long::sum);
            }
        }

        List<String> labels = monthlyOrders.keySet().stream()
                .map(month -> month.format(MONTH_LABEL))
                .toList();
        List<Number> values = new ArrayList<>(monthlyOrders.values());

        return chartResponse("bar", "Monthly Orders", labels, "Orders", values,
                "Here are your monthly orders:");
    }

    private AiResponse monthlySalesChart() {
        Map<YearMonth, Double> monthlySales = new TreeMap<>();
        for (Order order : userOrders()) {
            if (order.getCreatedAt() != null && !"CANCELLED".equalsIgnoreCase(order.getOrderStatus())) {
                monthlySales.merge(
                        YearMonth.from(order.getCreatedAt()),
                        order.getOrderTotalPrice() != null ? order.getOrderTotalPrice() : 0.0,
                        Double::sum
                );
            }
        }

        List<String> labels = monthlySales.keySet().stream()
                .map(month -> month.format(MONTH_LABEL))
                .toList();
        List<Number> values = new ArrayList<>(monthlySales.values());

        return chartResponse("line", "Sales Trend", labels, "Sales", values,
                "Here is your sales trend:");
    }

    private AiResponse orderStatusChart() {
        Map<String, Long> statusCounts = new TreeMap<>();
        for (Order order : userOrders()) {
            String status = order.getOrderStatus();
            statusCounts.merge(
                    status == null || status.isBlank() ? "UNKNOWN" : status,
                    1L,
                    Long::sum
            );
        }

        return chartResponse(
                "pie",
                "Orders by Status",
                new ArrayList<>(statusCounts.keySet()),
                "Orders",
                new ArrayList<>(statusCounts.values()),
                "Here is your order status breakdown:"
        );
    }

    private AiResponse chartResponse(String type,
                                     String title,
                                     List<String> labels,
                                     String seriesName,
                                     List<Number> values,
                                     String message) {
        Map<String, Object> series = new LinkedHashMap<>();
        series.put("name", seriesName);
        series.put("data", values);

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("type", type);
        chart.put("title", title);
        chart.put("labels", labels);
        chart.put("series", List.of(series));

        return new AiResponse("CHART", "SUCCESS", Map.of("chart", chart), message);
    }

    private List<Order> userOrders() {
        String userId = SessionContext.getUserId();
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> chartData(AiResponse response) {
        return (Map<String, Object>) response.getData();
    }

    private boolean isMonthlyOrdersRequest(String text) {
        return text.contains("order")
                && (text.contains("monthly") || text.contains("bar chart") || text.contains("bar graph"));
    }

    private boolean isSalesTrendRequest(String text) {
        boolean sales = text.contains("sales") || text.contains("revenue");
        return sales && (text.contains("trend") || text.contains("monthly")
                || text.contains("line chart") || text.contains("line graph"));
    }

    private boolean isOrderStatusRequest(String text) {
        boolean status = text.contains("order status") || text.contains("orders by status");
        return status && (text.contains("distribution") || text.contains("breakdown")
                || text.contains("pie") || text.contains("donut") || text.contains("chart")
                || text.startsWith("show"));
    }
}
