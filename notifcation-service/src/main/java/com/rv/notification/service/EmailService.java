package com.rv.notification.service;

import com.rv.notification.event.OrderEvent;
import com.rv.notification.event.OrderEventItem;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderCreatedEmail(OrderEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getCustomerEmail());
            helper.setSubject("We received your SwiftCart order - " + event.getOrderTrackingNum());
            helper.setText(buildOrderCreatedEmailBody(event), true);

            mailSender.send(message);
            log.info("Order created email sent successfully to: {}", event.getCustomerEmail());
        } catch (MessagingException e) {
            log.error("Failed to send order created email to: {}", event.getCustomerEmail(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendOrderCancelledEmail(OrderEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getCustomerEmail());
            helper.setSubject("Your SwiftCart Order Has Been Cancelled - " + event.getOrderTrackingNum());
            helper.setText(buildOrderCancelledEmailBody(event), true);

            mailSender.send(message);
            log.info("Order cancelled email sent successfully to: {}", event.getCustomerEmail());
        } catch (MessagingException e) {
            log.error("Failed to send order cancelled email to: {}", event.getCustomerEmail(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendInventoryFailureEmail(OrderEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getCustomerEmail());
            helper.setSubject("We Couldn’t Confirm Your SwiftCart Order");
            helper.setText(buildInventoryFailureEmailBody(event), true);

            mailSender.send(message);
            log.info("Inventory failure email sent successfully to: {}", event.getCustomerEmail());
        } catch (MessagingException e) {
            log.error("Failed to send inventory failure email to: {}", event.getCustomerEmail(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildOrderCreatedEmailBody(OrderEvent event) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; background-color:#f5f5f5; padding:20px;'>");
        html.append("<div style='max-width: 600px; margin: 0 auto; padding: 24px; background-color:#ffffff; border: 1px solid #e0e0e0; border-radius: 12px;'>");
        html.append("<h2 style='color: #2563eb; margin-bottom: 8px;'>Thank you for shopping with SwiftCart</h2>");
        html.append("<p style='margin: 0 0 16px 0;'>Hi,</p>");
        html.append("<p style='margin: 0 0 16px 0;'>We’re pleased to let you know that your SwiftCart order has been received and is now being processed.</p>");
        
        html.append("<div style='background-color: #f9fafb; padding: 16px; margin: 20px 0; border-radius: 8px;'>");
        html.append("<h3 style='margin: 0 0 12px 0;'>Order details</h3>");
        html.append("<p><strong>Order ID:</strong> ").append(event.getOrderId()).append("</p>");
        html.append("<p><strong>Tracking Number:</strong> ").append(event.getOrderTrackingNum()).append("</p>");
        
        if (event.getEventTime() != null) {
            String formattedDate = event.getEventTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
            html.append("<p style='margin: 4px 0;'><strong>Order date:</strong> ").append(formattedDate).append("</p>");
        }
        html.append("</div>");

        if (event.getItems() != null && !event.getItems().isEmpty()) {
            html.append("<h3 style='margin: 24px 0 12px 0;'>Items in your order</h3>");
            html.append("<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>");
            html.append("<tr style='background-color: #f3f4f6;'>");
            html.append("<th style='padding: 10px; text-align: left; border: 1px solid #e5e7eb;'>Product</th>");
            html.append("<th style='padding: 10px; text-align: center; border: 1px solid #e5e7eb;'>Quantity</th>");
            html.append("</tr>");
            
            for (OrderEventItem item : event.getItems()) {
                html.append("<tr>");
                html.append("<td style='padding: 10px; border: 1px solid #e5e7eb;'>")
                    .append("Product ID: ").append(item.getProductId())
                    .append("</td>");
                html.append("<td style='padding: 10px; text-align: center; border: 1px solid #e5e7eb;'>")
                    .append(item.getQuantity())
                    .append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        html.append("<div style='margin-top: 24px; padding-top: 16px; border-top: 1px solid #e5e7eb;'>");
        html.append("<p style='margin: 0 0 8px 0;'>You can track your order anytime using the tracking number above.</p>");
        html.append("<p style='margin: 0;'>If you have any questions, our SwiftCart support team is here to help.</p>");
        html.append("</div>");

        html.append("<p style='margin-top: 24px; font-size: 13px; color:#6b7280;'>");
        html.append("Thank you for choosing <strong>SwiftCart</strong>.<br/>");
        html.append("We hope to see you again soon.");
        html.append("</p>");
        html.append("</div>");
        html.append("</body></html>");
        
        return html.toString();
    }

    private String buildOrderCancelledEmailBody(OrderEvent event) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; background-color:#f5f5f5; padding:20px;'>");
        html.append("<div style='max-width: 600px; margin: 0 auto; padding: 24px; background-color:#ffffff; border: 1px solid #e0e0e0; border-radius: 12px;'>");
        html.append("<h2 style='color: #dc2626; margin-bottom: 8px;'>Your SwiftCart order has been cancelled</h2>");
        html.append("<p style='margin: 0 0 16px 0;'>Hi,</p>");
        html.append("<p style='margin: 0 0 16px 0;'>This is to confirm that your order has been cancelled.</p>");
        
        html.append("<div style='background-color: #fef3c7; padding: 16px; margin: 20px 0; border-radius: 8px; border-left: 4px solid #dc2626;'>");
        html.append("<h3 style='margin: 0 0 12px 0;'>Order details</h3>");
        html.append("<p><strong>Order ID:</strong> ").append(event.getOrderId()).append("</p>");
        html.append("<p><strong>Tracking Number:</strong> ").append(event.getOrderTrackingNum()).append("</p>");
        
        if (event.getEventTime() != null) {
            String formattedDate = event.getEventTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
            html.append("<p style='margin: 4px 0;'><strong>Cancellation date:</strong> ").append(formattedDate).append("</p>");
        }
        html.append("</div>");

        if (event.getItems() != null && !event.getItems().isEmpty()) {
            html.append("<h3 style='margin: 24px 0 12px 0;'>Items in this order</h3>");
            html.append("<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>");
            html.append("<tr style='background-color: #f3f4f6;'>");
            html.append("<th style='padding: 10px; text-align: left; border: 1px solid #e5e7eb;'>Product</th>");
            html.append("<th style='padding: 10px; text-align: center; border: 1px solid #e5e7eb;'>Quantity</th>");
            html.append("</tr>");
            
            for (OrderEventItem item : event.getItems()) {
                html.append("<tr>");
                html.append("<td style='padding: 10px; border: 1px solid #e5e7eb;'>")
                    .append("Product ID: ").append(item.getProductId())
                    .append("</td>");
                html.append("<td style='padding: 10px; text-align: center; border: 1px solid #e5e7eb;'>")
                    .append(item.getQuantity())
                    .append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        html.append("<div style='margin-top: 24px; padding-top: 16px; border-top: 1px solid #e5e7eb;'>");
        html.append("<p style='margin: 0 0 8px 0;'>If you did not request this cancellation or have any questions, please contact SwiftCart support immediately.</p>");
        html.append("<p style='margin: 0;'>Any payment made for this order will be refunded as per our standard timelines.</p>");
        html.append("</div>");

        html.append("<p style='margin-top: 24px; font-size: 13px; color:#6b7280;'>");
        html.append("Thank you for trying <strong>SwiftCart</strong>.<br/>");
        html.append("We hope to serve you again soon.");
        html.append("</p>");
        html.append("</div>");
        html.append("</body></html>");
        
        return html.toString();
    }

    private String buildInventoryFailureEmailBody(OrderEvent event) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; background-color:#f5f5f5; padding:20px;'>");
        html.append("<div style='max-width: 600px; margin: 0 auto; padding: 24px; background-color:#ffffff; border: 1px solid #e0e0e0; border-radius: 12px;'>");
        html.append("<h2 style='color: #f97316; margin-bottom: 8px;'>We couldn’t confirm your SwiftCart order</h2>");
        html.append("<p style='margin: 0 0 16px 0;'>Hi,</p>");
        html.append("<p style='margin: 0 0 16px 0;'>We’re sorry, but we were unable to confirm your order because one or more items are currently unavailable.</p>");

        html.append("<div style='background-color: #fef3c7; padding: 16px; margin: 20px 0; border-radius: 8px; border-left: 4px solid #f97316;'>");
        html.append("<h3 style='margin: 0 0 12px 0;'>Order details</h3>");
        if (event.getOrderId() != null) {
            html.append("<p><strong>Order ID:</strong> ").append(event.getOrderId()).append("</p>");
        }
        if (event.getOrderTrackingNum() != null) {
            html.append("<p><strong>Tracking Number:</strong> ").append(event.getOrderTrackingNum()).append("</p>");
        }
        if (event.getEventTime() != null) {
            String formattedDate = event.getEventTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
            html.append("<p style='margin: 4px 0;'><strong>Request date:</strong> ").append(formattedDate).append("</p>");
        }
        html.append("</div>");

        if (event.getItems() != null && !event.getItems().isEmpty()) {
            html.append("<h3 style='margin: 24px 0 12px 0;'>Items we could not reserve</h3>");
            html.append("<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>");
            html.append("<tr style='background-color: #f3f4f6;'>");
            html.append("<th style='padding: 10px; text-align: left; border: 1px solid #e5e7eb;'>Product</th>");
            html.append("<th style='padding: 10px; text-align: center; border: 1px solid #e5e7eb;'>Quantity</th>");
            html.append("</tr>");

            for (OrderEventItem item : event.getItems()) {
                html.append("<tr>");
                html.append("<td style='padding: 10px; border: 1px solid #e5e7eb;'>")
                    .append("Product ID: ").append(item.getProductId())
                    .append("</td>");
                html.append("<td style='padding: 10px; text-align: center; border: 1px solid #e5e7eb;'>")
                    .append(item.getQuantity())
                    .append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        html.append("<div style='margin-top: 24px; padding-top: 16px; border-top: 1px solid #e5e7eb;'>");
        html.append("<p style='margin: 0 0 8px 0;'>No payment has been captured for this order. You can place a new order once the items are back in stock.</p>");
        html.append("<p style='margin: 0;'>If you have any questions, our SwiftCart support team will be happy to assist you.</p>");
        html.append("</div>");

        html.append("<p style='margin-top: 24px; font-size: 13px; color:#6b7280;'>");
        html.append("Thank you for your interest in <strong>SwiftCart</strong>.<br/>");
        html.append("We look forward to serving you in the future.");
        html.append("</p>");
        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }
}