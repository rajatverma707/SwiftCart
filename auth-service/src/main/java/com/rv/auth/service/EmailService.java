package com.rv.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.Message;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String body) {
        try {
            mailSender.send(message -> {
                message.setRecipients(Message.RecipientType.TO, to);
                message.setSubject(subject);
                message.setText(body);
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
