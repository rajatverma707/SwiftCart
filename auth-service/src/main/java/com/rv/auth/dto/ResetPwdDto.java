package com.rv.auth.dto;

import lombok.Data;

@Data
public class ResetPwdDto {

    private String email;
    private String newPwd;
    private String confirmPwd;
}
