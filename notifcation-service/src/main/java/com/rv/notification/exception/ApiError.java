package com.rv.notification.exception;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    private String error;
    private String message;
    private int status;
    private String path;
    private String traceId;
    private LocalDateTime timestamp;
}