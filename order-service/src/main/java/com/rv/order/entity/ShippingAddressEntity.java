package com.rv.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "SHIPPING_ADDRESS")
public class ShippingAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addr_id")
    private Long addrId;

    @Column(name = "house_num", length = 100)
    private String houseNum;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "zipcode", length = 20)
    private String zipcode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "addr_type", length = 500)
    private String addrType;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private UserEntity user;
}



