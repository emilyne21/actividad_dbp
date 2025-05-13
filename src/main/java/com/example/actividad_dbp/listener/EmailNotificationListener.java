package com.example.actividad_dbp.listener;

import com.example.actividad_dbp.event.OrderCreatedEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class EmailNotificationListener {

    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Sending confirmation email to: {}", event.getCustomerEmail());
        log.info("Email subject: Order Confirmation - Order ID: {}", event.getOrderId());

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Thank you for your order!\n\n");
        emailBody.append("Order Details:\n");
        emailBody.append("Order ID: ").append(event.getOrderId()).append("\n");
        emailBody.append("Order Date: ").append(event.getCreatedAt()).append("\n\n");
        emailBody.append("Items:\n");

        double total = 0.0;
        for (OrderCreatedEvent.OrderItem item : event.getItems()) {
            double itemTotal = item.getProductPrice() * item.getQuantity();
            total += itemTotal;
            emailBody.append("- ").append(item.getProductName())
                    .append(" (").append(item.getQuantity()).append(")")
                    .append(" $").append(item.getProductPrice())
                    .append(" = $").append(itemTotal).append("\n");
        }

        emailBody.append("\nTotal: $").append(total);

        log.info("Email content: \n{}", emailBody.toString());
        log.info("Email sent successfully to: {}", event.getCustomerEmail());
    }
}
