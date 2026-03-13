package com.rv.inventory.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(404)
                .body(ApiError.builder()
                        .errorCode("PRODUCT_NOT_FOUND")
                        .message("Inventory not found for product")
                        .status(404)
                        .path(request.getRequestURI())
                        .traceId(MDC.get("traceId"))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(500)
                .body(ApiError.builder()
                        .errorCode("INVENTORY_SERVICE_ERROR")
                        .message("Inventory service unavailable")
                        .status(500)
                        .path(request.getRequestURI())
                        .traceId(MDC.get("traceId"))
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
