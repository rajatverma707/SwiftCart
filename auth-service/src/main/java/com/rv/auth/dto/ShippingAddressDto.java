package com.rv.auth.dto;

import lombok.Data;

@Data
public class ShippingAddressDto {

    private Long addrId;
    private String houseNum;
    private String city;
    private String state;
    private String zipcode;
    private String country;
    private String addrType;

}
