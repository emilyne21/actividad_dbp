package com.example.actividad_dbp.listener;

import com.example.actividad_dbp.event.OrderCreatedEvent;
import com.example.actividad_dbp.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

@Component
@Log4j2
@RequiredArgsConstructor
public class InventoryUpdateListener {

    private final EntityManager entityManager;

    @EventListener
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Updating inventory for order: {}", event.getOrderId());

        for (OrderCreatedEvent.OrderItem item : event.getItems()) {
            try {
                Product product = entityManager.find(Product.class, item.getProductId());

                if (product == null) {
                    log.error("Product not found with ID: {}", item.getProductId());
                    continue;
                }

                int newStock = product.getStockQuantity() - item.getQuantity();

                if (newStock < 0) {
                    log.warn("Insufficient stock for product: {}. Current stock: {}, Requested: {}", 
                            product.getName(), product.getStockQuantity(), item.getQuantity());
                    // In a real application, you might want to throw an exception or handle this differently
                    continue;
                }

                product.setStockQuantity(newStock);
                entityManager.merge(product);

                log.info("Updated stock for product: {}. New stock: {}", 
                        product.getName(), newStock);
            } catch (Exception e) {
                log.error("Error updating inventory for product ID: {}", item.getProductId(), e);
            }
        }

        log.info("Inventory update completed for order: {}", event.getOrderId());
    }
}
