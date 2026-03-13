package com.rv.notification.repository;

import com.rv.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
