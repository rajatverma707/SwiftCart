package com.rv.order.exception;

import com.rv.order.dto.inventory.InventoryUnavailableItem;
import com.rv.order.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ApiResponse<Object>> handleOutOfStock(OutOfStockException ex) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setStatus(409);
        response.setMsg(ex.getMessage());

        // Include per-product shortage details in the response (if available)
        java.util.List<InventoryUnavailableItem> unavailable = ex.getUnavailableItems();
        response.setData(unavailable);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
