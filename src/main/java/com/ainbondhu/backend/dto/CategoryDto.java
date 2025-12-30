package com.ainbondhu.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryDto {
    private String id;
    private String nameBn;
    private String descriptionBn;
}
