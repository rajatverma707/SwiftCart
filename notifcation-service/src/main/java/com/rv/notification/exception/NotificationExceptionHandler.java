package com.rv.notification.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class NotificationExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleNotificationFailure(
            Exception ex,
            HttpServletRequest request) {

        String traceId = MDC.get("traceId");
        
        logger.error("Notification failed - TraceId: {}, Error: {}", traceId, ex.getMessage(), ex);

        ApiError err = ApiError.builder()
                .error("NOTIFICATION_FAILED")
                .message("Unable to send notification")
                .status(500)
                .path(request.getRequestURI())
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(500).body(err);
    }
}