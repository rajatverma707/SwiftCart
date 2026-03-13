package com.rv.auth.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Integer userId;
    private String name;
    private String email;
    private String pwd;
    private Long phno;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private String pwdUpdated;
    public String roleName;
}
