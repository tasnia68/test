package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.dto.LawyerDto;
import com.ainbondhu.backend.dto.RegisterRequest;
import com.ainbondhu.backend.dto.UserDto;
import com.ainbondhu.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/client")
    public ResponseEntity<UserDto> registerClient(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerClient(request));
    }

    @PostMapping("/register/lawyer")
    public ResponseEntity<LawyerDto> registerLawyer(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerLawyer(request));
    }

    @PostMapping("/login")
    public ResponseEntity<com.ainbondhu.backend.dto.LoginResponse> login(@RequestBody com.ainbondhu.backend.dto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
