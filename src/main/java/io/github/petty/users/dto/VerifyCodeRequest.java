package io.github.petty.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {
    private String email;               // 사용자 이메일
    private String verificationCode;    // 인증 코드
}