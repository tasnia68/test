package com.ainbondhu.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String id;
    private String phoneNumber;
    private String fullNameBn;
    private String role;
}
