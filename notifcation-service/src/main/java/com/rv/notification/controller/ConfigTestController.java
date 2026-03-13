package com.rv.notification.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@RestController
public class ConfigTestController {

    private final Environment env;

    public ConfigTestController(Environment env) {
        this.env = env;
    }

    @GetMapping("/config/email-subject")
    public String getEmailSubject() {
        return env.getProperty("notification.email.subject");
    }
}
