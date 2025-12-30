package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.dto.CategoryDto;
import com.ainbondhu.backend.dto.QuestionDto;
import com.ainbondhu.backend.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(scenarioService.getAllCategories());
    }

    @GetMapping("/{categoryId}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestions(@PathVariable String categoryId) {
        return ResponseEntity.ok(scenarioService.getQuestionsByCategory(categoryId));
    }
}
