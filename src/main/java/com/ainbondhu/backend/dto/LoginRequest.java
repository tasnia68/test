package com.ainbondhu.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    // OTP would be here in real app
}
