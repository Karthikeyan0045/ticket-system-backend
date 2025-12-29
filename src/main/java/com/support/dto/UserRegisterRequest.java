package com.support.dto;

import com.support.entity.UserRole;
import lombok.Data;

@Data
public class UserRegisterRequest {
    private String email;
    private String password;
    private String name;
    private UserRole role;
}
