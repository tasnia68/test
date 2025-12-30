package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.Lawyer;
import com.ainbondhu.backend.domain.entity.User;
import com.ainbondhu.backend.domain.enums.Role;
import com.ainbondhu.backend.domain.enums.VerificationStatus;
import com.ainbondhu.backend.dto.LawyerDto;
import com.ainbondhu.backend.dto.LoginRequest;
import com.ainbondhu.backend.dto.LoginResponse;
import com.ainbondhu.backend.dto.RegisterRequest;
import com.ainbondhu.backend.dto.UserDto;
import com.ainbondhu.backend.repository.LawyerRepository;
import com.ainbondhu.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // In real OTP flow: verify OTP here.
        // For now, we assume if user exists, they are logged in (Mock OTP)
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public UserDto registerClient(RegisterRequest request) {
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }

        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFullNameBn(request.getFullNameBn());
        user.setRole(Role.CLIENT);
        user.setLocation(createPoint(request.getLatitude(), request.getLongitude()));
        user.setPassword(passwordEncoder.encode("password")); // Dummy password for Spring Security

        user = userRepository.save(user);

        return UserDto.builder()
                .id(user.getId().toString())
                .phoneNumber(user.getPhoneNumber())
                .fullNameBn(user.getFullNameBn())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public LawyerDto registerLawyer(RegisterRequest request) {
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }

        Lawyer lawyer = new Lawyer();
        lawyer.setPhoneNumber(request.getPhoneNumber());
        lawyer.setFullNameBn(request.getFullNameBn());
        lawyer.setRole(Role.LAWYER);
        lawyer.setLocation(createPoint(request.getLatitude(), request.getLongitude()));
        lawyer.setPassword(passwordEncoder.encode("password")); // Dummy password

        lawyer.setBarLicenseNumber(request.getBarLicenseNumber());
        lawyer.setEducationalBackground(request.getEducationalBackground());
        lawyer.setExperienceYears(request.getExperienceYears());
        lawyer.setVerificationStatus(VerificationStatus.PENDING);
        lawyer.setOnline(false); // Offline by default until verified/toggled

        lawyer = lawyerRepository.save(lawyer);

        return LawyerDto.builder()
                .id(lawyer.getId().toString())
                .phoneNumber(lawyer.getPhoneNumber())
                .fullNameBn(lawyer.getFullNameBn())
                .barLicenseNumber(lawyer.getBarLicenseNumber())
                .verificationStatus(lawyer.getVerificationStatus().name())
                .isOnline(lawyer.isOnline())
                .build();
    }

    private Point createPoint(double lat, double lon) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}
