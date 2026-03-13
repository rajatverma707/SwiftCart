package com.rv.order.dto;

import lombok.Data;

@Data
public class AddressDto {

    private Integer addrId;
    private String houseNo;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String addrType;

}

