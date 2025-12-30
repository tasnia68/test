package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.dto.LawyerDto;
import com.ainbondhu.backend.service.LawyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lawyers")
@RequiredArgsConstructor
public class LawyerController {

    private final LawyerService lawyerService;

    @GetMapping("/nearby")
    public ResponseEntity<List<LawyerDto>> getNearbyLawyers(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5000") double radius) {
        return ResponseEntity.ok(lawyerService.findNearbyLawyers(lat, lon, radius));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatus(@RequestParam boolean isOnline) {
        // In real app, get ID from security context.
        // Assuming we have SecurityContext, we can extract it.
        // For simplicity in this demo, we might need to pass ID or rely on Auth.
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // We'll leave the implementation stubbed as "Needs Auth Context" but call the service if we had the ID.
        // For now, let's allow passing ID via param just for testing the loop (NOT SECURE for prod).

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatusById(@PathVariable String id, @RequestParam boolean isOnline) {
         lawyerService.updateOnlineStatus(id, isOnline);
         return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LawyerDto> getLawyerById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(lawyerService.getLawyerById(id));
    }
}
