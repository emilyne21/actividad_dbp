package com.example.actividad_dbp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for the Order Management System.
 * 
 * This application demonstrates the Observer pattern using Spring's event system:
 * - OrderCreatedEvent is published when a new order is created
 * - Multiple listeners (EmailNotificationListener, InventoryUpdateListener, AuditLogListener)
 *   respond to the event
 * 
 * The @EnableAsync annotation allows event listeners to be executed asynchronously
 * when they are annotated with @Async.
 */
@SpringBootApplication
@EnableAsync
public class ActividadDbpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActividadDbpApplication.class, args);
    }

    /**
     * Configures an asynchronous event multicaster to allow events to be processed
     * in separate threads, preventing blocking of the main thread.
     * 
     * @return ApplicationEventMulticaster that uses a SimpleAsyncTaskExecutor
     */
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }
}
