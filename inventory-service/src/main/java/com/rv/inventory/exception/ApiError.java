package com.rv.inventory.exception;

import java.time.LocalDateTime;

public class ApiError {
    private String errorCode;
    private String message;
    private int status;
    private String path;
    private String traceId;
    private LocalDateTime timestamp;

    private ApiError() {}

    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public String getPath() { return path; }
    public String getTraceId() { return traceId; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ApiError e = new ApiError();

        public Builder errorCode(String code) { e.errorCode = code; return this; }
        public Builder message(String msg) { e.message = msg; return this; }
        public Builder status(int s) { e.status = s; return this; }
        public Builder path(String p) { e.path = p; return this; }
        public Builder traceId(String t) { e.traceId = t; return this; }
        public Builder timestamp(LocalDateTime ts) { e.timestamp = ts; return this; }
        public ApiError build() { return e; }
    }
}
