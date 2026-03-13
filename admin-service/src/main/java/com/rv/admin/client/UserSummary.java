package com.rv.admin.client;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserSummary {

    private Integer userId;
    private String name;
    private String email;
    private String pwd;
    private Long phno;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private String pwdUpdated;
    private String roleName;
}
