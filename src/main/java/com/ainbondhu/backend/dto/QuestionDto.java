package com.ainbondhu.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionDto {
    private String id;
    private String textBn;
    private int order;
    private List<String> options;
}
