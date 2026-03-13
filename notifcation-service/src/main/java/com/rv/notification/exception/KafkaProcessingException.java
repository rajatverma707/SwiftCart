package com.rv.notification.exception;

public class KafkaProcessingException extends ApplicationException {
    public KafkaProcessingException(String message) { super(message); }
    public KafkaProcessingException(String message, Throwable cause) { super(message, cause); }
}
