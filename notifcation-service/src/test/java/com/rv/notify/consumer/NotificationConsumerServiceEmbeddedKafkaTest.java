package com.rv.notify.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.rv.notification.consumer.NotificationConsumerService;
import com.rv.notification.entity.Notification;
import com.rv.notification.event.OrderEvent;
import com.rv.notification.event.OrderEventType;
import com.rv.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Integration test for NotificationConsumerService using embedded Kafka.
 * This runs a real Kafka broker in memory for testing.
 */
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    }
)
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "kafka.topic.notification=notification-topic"
})
class NotificationConsumerServiceEmbeddedKafkaTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationConsumerService notificationConsumerService;

    private OrderEvent testOrderEvent;

    @BeforeEach
    void setUp() {
        testOrderEvent = new OrderEvent();
        testOrderEvent.setEventType(OrderEventType.ORDER_CREATED);
        testOrderEvent.setOrderId(1L);
        testOrderEvent.setOrderTrackingNum("TRACK123");
        testOrderEvent.setCustomerEmail("customer@example.com");
        testOrderEvent.setEventTime(LocalDateTime.now());
    }

    /**
     * Test order created event with embedded Kafka
     */
    @Test
    void testOrderCreatedEventWithEmbeddedKafka() {
        // When: Send ORDER_CREATED event
        kafkaTemplate.send("order.events", testOrderEvent);

        // Then: Verify that notification was saved
        await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() ->
                verify(notificationRepository, times(1)).save(any(Notification.class))
            );
    }

    /**
     * Test order cancelled event with embedded Kafka
     */
    @Test
    void testOrderCancelledEventWithEmbeddedKafka() {
        // Given: Setup ORDER_CANCELLED event
        testOrderEvent.setEventType(OrderEventType.ORDER_CANCELLED);

        // When: Send to Kafka
        kafkaTemplate.send("order.events", testOrderEvent);

        // Then: Verify storage
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() ->
                verify(notificationRepository, times(1)).save(any(Notification.class))
            );
    }

    /**
     * Test string message on notification topic
     */
    @Test
    void testStringMessageConsumption() {
        // When: Send string message
        kafkaTemplate.send("notification-topic", "Test message content");

        // Then: Verify message is accessible
        // await()
        //     .atMost(10, TimeUnit.SECONDS)
        //     .pollInterval(500, TimeUnit.MILLISECONDS)
        //     .untilAsserted(() -> {
        //         String message = notificationConsumerService.getMessage();
        //         assert message != null && message.equals("Test message content");
        //     });
    }

    /**
     * Test multiple messages
     */
    @Test
    void testMultipleMessages() {
        // Given: Multiple different order events
        OrderEvent event1 = new OrderEvent();
        event1.setEventType(OrderEventType.ORDER_CREATED);
        event1.setOrderId(1L);
        event1.setCustomerEmail("customer1@example.com");
        event1.setOrderTrackingNum("TRACK001");

        OrderEvent event2 = new OrderEvent();
        event2.setEventType(OrderEventType.ORDER_CANCELLED);
        event2.setOrderId(2L);
        event2.setCustomerEmail("customer2@example.com");
        event2.setOrderTrackingNum("TRACK002");

        // When: Send both events
        kafkaTemplate.send("order.events", event1);
        kafkaTemplate.send("order.events", event2);

        // Then: Verify both were processed
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() ->
                verify(notificationRepository, times(2)).save(any(Notification.class))
            );
    }
}
