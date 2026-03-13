package com.rv.user.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserResponseDTO {

    private Integer userId;
    private String name;
    private String email;
    private Long phoneNumber;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private String roleName;
}

