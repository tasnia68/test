package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.domain.entity.CaseRequest;
import com.ainbondhu.backend.repository.CaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseRequestRepository caseRequestRepository;

    @PostMapping
    public ResponseEntity<CaseRequest> requestLawyer(@RequestParam String clientId, @RequestParam String lawyerId) {
        CaseRequest request = new CaseRequest();
        request.setClientId(clientId);
        request.setLawyerId(lawyerId);
        request.setStatus("REQUESTED");
        request.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(caseRequestRepository.save(request));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Void> acceptCase(@PathVariable String id) {
        CaseRequest request = caseRequestRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Case Request not found"));
        request.setStatus("ACCEPTED");
        caseRequestRepository.save(request);
        return ResponseEntity.ok().build();
    }
}
