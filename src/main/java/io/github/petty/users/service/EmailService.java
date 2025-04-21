package io.github.petty.users.service;

import io.github.petty.users.entity.EmailVerification;
import io.github.petty.users.repository.EmailVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;
    private final Random random = new Random();

    // 인증 코드 생성 + 저장 + 이메일 전송
    public boolean sendVerificationCode(String email) {
        String code = generateCode();

        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setCode(code);
        emailVerificationRepository.save(verification);

        try {
            sendEmail(email, code);
            return true;
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
            return false;
        }
    }

    // 4자리 랜덤 코드 생성
    private String generateCode() {
        return String.format("%04d", random.nextInt(10000)); // 0000 ~ 9999
    }

    private void sendEmail(String toEmail, String code) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        helper.setFrom("krpetty54@gmail.com");  // 보내는 사람 이메일 (본인의 실제 이메일 주소로 변경)
        helper.setTo(toEmail);
        helper.setSubject("[Petty] 이메일 인증 코드입니다.");
        helper.setText(String.format("안녕하세요.\n\n요청하신 이메일 인증 코드는 다음과 같습니다.\n\n%s\n\n감사합니다.", code));

        javaMailSender.send(mimeMessage);
        log.info("이메일 ({})로 인증 코드 ({})를 성공적으로 전송했습니다.", toEmail, code);
    }
}