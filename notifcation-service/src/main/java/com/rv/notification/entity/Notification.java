package com.rv.notification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String orderTrackingNum;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String type; // ORDER_CREATED, ORDER_CANCELLED

    @Column(nullable = false)
    private String status; // PENDING, SENT, FAILED, RETRY

    @Column(name = "notification_channel")
    private String notificationChannel = "EMAIL"; // EMAIL, SMS, PUSH

    private Integer retryCount = 0;

    @Column(length = 500)
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
