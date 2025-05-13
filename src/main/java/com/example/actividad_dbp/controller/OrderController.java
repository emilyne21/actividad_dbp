package com.example.actividad_dbp.controller;

import com.example.actividad_dbp.dto.OrderRequest;
import com.example.actividad_dbp.event.OrderCreatedEvent;
import com.example.actividad_dbp.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final ApplicationEventPublisher eventPublisher;
    private final EntityManager entityManager;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        log.info("Received order request from: {}", orderRequest.getCustomerEmail());

        try {
            // Generate a unique order ID
            UUID orderId = UUID.randomUUID();

            // Create a list to store order items
            List<OrderCreatedEvent.OrderItem> orderItems = new ArrayList<>();

            // Process each item in the order request
            for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
                // Find the product by ID
                Product product = entityManager.find(Product.class, itemRequest.getProductId());

                if (product == null) {
                    return ResponseEntity.badRequest()
                            .body("Product not found with ID: " + itemRequest.getProductId());
                }

                // Check if there's enough stock
                if (product.getStockQuantity() < itemRequest.getQuantity()) {
                    return ResponseEntity.badRequest()
                            .body("Insufficient stock for product: " + product.getName() + 
                                  ". Available: " + product.getStockQuantity() + 
                                  ", Requested: " + itemRequest.getQuantity());
                }

                // Create an order item
                OrderCreatedEvent.OrderItem orderItem = new OrderCreatedEvent.OrderItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        itemRequest.getQuantity()
                );

                orderItems.add(orderItem);
            }

            // Create the order event
            OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                    orderId,
                    orderRequest.getCustomerEmail(),
                    orderItems,
                    LocalDateTime.now()
            );

            // Publish the event
            log.info("Publishing OrderCreatedEvent for order: {}", orderId);
            eventPublisher.publishEvent(orderCreatedEvent);

            // Return a success response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Order created successfully with ID: " + orderId);

        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }
}
