package com.rv.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_tracking_num", length = 255)
    private String orderTrackingNum;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "order_status", length = 50)
    private String orderStatus;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(name = "razorpay_order_id", length = 255)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 255)
    private String razorpayPaymentId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "shipping_addr_id", referencedColumnName = "addr_id")
    private ShippingAddressEntity shippingAddress;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
