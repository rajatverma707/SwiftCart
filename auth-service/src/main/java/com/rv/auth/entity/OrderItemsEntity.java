package com.rv.auth.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ORDER_ITEMS")
@Getter
@Setter
public class OrderItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private OrderEntity order;
}
