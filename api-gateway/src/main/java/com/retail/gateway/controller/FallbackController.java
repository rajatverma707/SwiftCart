package com.retail.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @RequestMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.ok("Service is temporarily unavailable. Please try again later.");
    }
}
