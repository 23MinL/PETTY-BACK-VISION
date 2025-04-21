package io.github.petty.users.repository;

import io.github.petty.users.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    // 이메일로 가장 최근 인증 조회
    EmailVerification findTopByEmailOrderByIdDesc(String email);

    // 가입 성공 이메일 모두 삭제
    void deleteByEmail(String email);
}
