package com.example.actividad_dbp.listener;

import com.example.actividad_dbp.event.OrderCreatedEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Log4j2
@Order(3) // Execute after other listeners
public class AuditLogListener {

    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("AUDIT: New order created");
        log.info("AUDIT: Order ID: {}", event.getOrderId());
        log.info("AUDIT: Customer Email: {}", event.getCustomerEmail());
        log.info("AUDIT: Order Date: {}", event.getCreatedAt());

        String itemsLog = event.getItems().stream()
                .map(item -> String.format(
                        "Product ID: %d, Name: %s, Price: $%.2f, Quantity: %d, Total: $%.2f",
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductPrice(),
                        item.getQuantity(),
                        item.getProductPrice() * item.getQuantity()
                ))
                .collect(Collectors.joining("\n"));

        log.info("AUDIT: Order Items:\n{}", itemsLog);

        double orderTotal = event.getItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();

        log.info("AUDIT: Order Total: ${}", orderTotal);
        log.info("AUDIT: Order logging completed");
    }
}
