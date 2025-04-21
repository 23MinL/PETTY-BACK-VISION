package io.github.petty.users.controller;

import io.github.petty.users.dto.EmailVerificationRequest;
import io.github.petty.users.dto.VerifyCodeRequest;
import io.github.petty.users.jwt.JWTUtil;
import io.github.petty.users.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersApiController {

    private final JWTUtil jwtUtil;
    private final EmailService emailService;

    public UsersApiController(JWTUtil jwtUtil, EmailService emailService) {
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @GetMapping("/users/me")
    public ResponseEntity<Map<String, String>> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", auth.getName());
        userInfo.put("role", auth.getAuthorities().iterator().next().getAuthority());

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/auth/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerification(@RequestBody EmailVerificationRequest request) {
        // 인증 코드 생성 및 이메일 발송 로직
        boolean success = emailService.sendVerificationCode(request.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);

        return ResponseEntity.ok(response);
    }
}